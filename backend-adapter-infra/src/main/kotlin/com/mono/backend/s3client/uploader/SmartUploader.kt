package com.mono.backend.s3client.uploader

import com.mono.backend.log.logger
import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.ProgressCallback
import com.mono.backend.s3client.model.UploadStatus
import com.mono.backend.s3client.retry.RetryEngine
import com.mono.backend.s3client.storage.UploadStorageProvider
import com.mono.backend.s3client.util.AwsSdkUtils
import com.mono.backend.s3client.util.FileUtils.chunkedFlow
import com.mono.backend.s3client.util.FileUtils.combine
import com.mono.backend.s3client.util.FileUtils.toByteArray
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.util.concurrent.atomic.AtomicLong

@Component
class SmartUploader(
    private val s3Client: S3AsyncClient,
    private val config: UploadConfig,
    private val resumeStorage: UploadStorageProvider,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) {
    private val log = logger()

    suspend fun upload(fileKey: String, file: FilePart, progressCallback: ProgressCallback?): FileResponse {
        var uploadStatus: UploadStatus? = resumeStorage.load(fileKey)
        val completedParts = uploadStatus?.completeParts?.values?.toMutableList() ?: mutableListOf()
        val uploadedBytes = AtomicLong(completedParts.sumOf { 0L })
        val bufferList = mutableListOf<ByteArray>()
        var totalSize = uploadedBytes.get()

        file.content().asFlow()
            .map { dataBuffer -> dataBuffer.toByteArray().also { DataBufferUtils.release(dataBuffer) } }
            .chunkedFlow(config.maxParallelUploads)
            .collect { batch ->
                batch.forEach { bytes ->
                    bufferList += bytes
                    totalSize += bytes.size

                    // 업로드 결정 시점, threshold 값 초과시 multipart로 업로드 결정
                    if (uploadStatus == null && totalSize > config.multipartThreshold) {
                        uploadStatus = startMultipart(fileKey, file)
                    }

                    if (uploadStatus == null) return@collect
                    if (bufferList.sumOf { it.size } < config.multipartPartSize) return@collect

                    uploadBatchParallel(
                        bufferList,
                        uploadStatus!!,
                        completedParts,
                        uploadedBytes,
                        progressCallback
                    ).awaitAll()
                    bufferList.clear()
                    resumeStorage.save(uploadStatus!!)
                }
            }

        return if (uploadStatus == null) {
            // 싱글 파트 결정
            uploadSinglePart(fileKey, file, bufferList.combine())
        } else {
            // 멀티파트 마지막 청크
            if (bufferList.isNotEmpty()) {
                uploadBatchParallel(
                    bufferList,
                    uploadStatus!!,
                    completedParts,
                    uploadedBytes,
                    progressCallback
                ).awaitAll()
            }
            val partsOrdered = completedParts.sortedBy { it.partNumber() }

            val completeResponse = RetryEngine.run {
                val multipartUpload = CompletedMultipartUpload.builder().parts(partsOrdered).build()
                val request = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(uploadStatus!!.fileKey)
                    .uploadId(uploadStatus!!.uploadId)
                    .multipartUpload(multipartUpload)
                    .build()
                s3Client.completeMultipartUpload(request).await()
            }
            AwsSdkUtils.checkSdkResponse(completeResponse)
            resumeStorage.delete(uploadStatus!!.uploadId)

            FileResponse(
                name = file.filename(),
                uploadId = uploadStatus!!.uploadId,
                path = completeResponse.location(),
                type = uploadStatus!!.contentType,
                eTag = completeResponse.eTag()
            )
        }
    }

    private suspend fun uploadBatchParallel(
        bufferList: MutableList<ByteArray>,
        uploadStatus: UploadStatus,
        completedParts: MutableList<CompletedPart>,
        uploadedBytes: AtomicLong,
        progressCallback: ProgressCallback?
    ) = coroutineScope {
        bufferList
            .map { it to uploadStatus.getAddedPartCounter() }
            .map { (data, partNumber) ->
                async {
                    val request = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(uploadStatus.fileKey)
                        .uploadId(uploadStatus.uploadId)
                        .partNumber(partNumber)
                        .contentLength(data.size.toLong())
                        .build()

                    val asyncBody = AsyncRequestBody.fromBytes(data)
                    val response = s3Client.uploadPart(request, asyncBody).await()
                    AwsSdkUtils.checkSdkResponse(response)

                    val part = CompletedPart.builder().partNumber(partNumber).eTag(response.eTag()).build()
                    synchronized(completedParts) { completedParts += part }
                    uploadStatus.completeParts[partNumber] = part
                    uploadedBytes.addAndGet(data.size.toLong())
                    progressCallback?.onProgress(uploadedBytes.get(), -1)
                }
            }
    }

    suspend fun uploadSinglePart(fileKey: String, file: FilePart, data: ByteArray): FileResponse {
        log.info("[SinglePartUploader.uploadSinglePart] uploading file($fileKey) / size : ${data.size.toLong()} to S3...")
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentLength(data.size.toLong())
            .contentType(contentType.toString())
            .build()

        val response = RetryEngine.run {
            val asyncBody = AsyncRequestBody.fromBytes(data)
            s3Client.putObject(request, asyncBody).await()
        }
        AwsSdkUtils.checkSdkResponse(response)

        return FileResponse(
            name = file.filename(),
            uploadId = "",
            path = "$localEndpoint/$bucketName/$fileKey",
            type = "",
            eTag = response.eTag()
        )
    }

    suspend fun startMultipart(fileKey: String, file: FilePart): UploadStatus {
        log.info("[SinglePartUploader.startMultipart] start uploading file($fileKey, ${file.filename()}) to S3...")
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM

        val createResponse = RetryEngine.run {
            s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType(contentType.toString())
                    .metadata(mapOf("filename" to file.filename()))
                    .key(fileKey)
                    .bucket(bucketName)
                    .build()
            ).await()
        }

        AwsSdkUtils.checkSdkResponse(createResponse)
        return UploadStatus(
            contentType = contentType.toString(),
            fileKey = fileKey,
            uploadId = createResponse.uploadId(),
            buffered = 0
        ).also { resumeStorage.save(it) }
    }
}