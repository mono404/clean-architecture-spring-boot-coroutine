package com.mono.backend.s3client.uploader

import com.mono.backend.log.logger
import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.ProgressCallback
import com.mono.backend.s3client.model.UploadStatus
import com.mono.backend.s3client.storage.UploadStorageProvider
import com.mono.backend.s3client.util.AwsSdkUtils
import com.mono.backend.s3client.util.FileUtils.combine
import com.mono.backend.s3client.util.FileUtils.toByteArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
class SmartUploaderV2(
    private val s3Client: S3AsyncClient,
    private val config: UploadConfig,
    private val resumeStorage: UploadStorageProvider,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) {
    private val log = logger()

    suspend fun upload(fileKey: String, file: FilePart, progressCallback: ProgressCallback?): FileResponse {
        var totalSize = 0L
        val bufferList = mutableListOf<ByteArray>()
        val completedParts = mutableListOf<CompletedPart>()
        val uploadedBytes = AtomicLong(0)
        var uploadStatus: UploadStatus? = null

        coroutineScope {
            file.content().asFlow()
                .map { dataBuffer -> dataBuffer.toByteArray().also { DataBufferUtils.release(dataBuffer) } }
                .collect { chunk ->
                    bufferList.add(chunk)
                    totalSize += chunk.size

                    // 업로드 결정 시점, threshold 값 초과시 multipart로 업로드 결정
                    if (uploadStatus == null && totalSize > config.multipartThreshold) {
                        //multipart init
                        uploadStatus = initializeUpload(fileKey, file)

                        // 이미 읽은 버퍼들로 바로 첫 파트로 업로드 시작
                        val firstChunk = bufferList.combine()
                        val firstPart = uploadPart(uploadStatus!!, 1, firstChunk)
                        completedParts.add(firstPart)
                        uploadedBytes.addAndGet(firstChunk.size.toLong())
                        progressCallback?.onProgress(uploadedBytes.get(), -1)
                        bufferList.clear()
                    }

                    // multipart 업로드 중, 파트 사이즈 넘으면 병렬 업로드 실행
                    if (uploadStatus != null && bufferList.sumOf { it.size.toLong() } >= config.multipartPartSize) {
                        val batchChunk = bufferList.combine()
                        val partNumber = uploadStatus!!.getAddedPartCounter()
                        val part = uploadPart(uploadStatus!!, partNumber, batchChunk)
                        completedParts.add(part)
                        uploadedBytes.addAndGet(batchChunk.size.toLong())
                        progressCallback?.onProgress(uploadedBytes.get(), -1)
                        bufferList.clear()
                    }
                }
        }

        return if (uploadStatus == null) {
            // 싱글 파트 결정
            val fullData = bufferList.combine()
            val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
            val request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentLength(fullData.size.toLong())
                .contentType(contentType.toString())
                .acl("public-read")
                .build()

            val asyncBody = AsyncRequestBody.fromBytes(fullData)
            val response = s3Client.putObject(request, asyncBody).await()
            AwsSdkUtils.checkSdkResponse(response)

            FileResponse(
                name = file.filename(),
                uploadId = "",
                path = "$localEndpoint/$bucketName/$fileKey",
                type = "",
                eTag = response.eTag()
            )
        } else {
            // 멀티파트 마지막 청크
            if (bufferList.isNotEmpty()) {
                val chunk = bufferList.combine()
                val partNumber = uploadStatus!!.getAddedPartCounter()
                val part = uploadPart(uploadStatus!!, partNumber, chunk)
                completedParts.add(part)
            }

            val completeResponse = completeMultipart(uploadStatus!!, completedParts)
            FileResponse(
                name = file.filename(),
                uploadId = uploadStatus!!.uploadId,
                path = completeResponse.location(),
                type = uploadStatus!!.contentType,
                eTag = completeResponse.eTag()
            )
        }
    }

    private suspend fun completeMultipart(
        uploadStatus: UploadStatus,
        parts: MutableList<CompletedPart>
    ): CompleteMultipartUploadResponse {
        val multipartUpload = CompletedMultipartUpload.builder().parts(parts).build()
        val request = CompleteMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(uploadStatus.fileKey)
            .uploadId(uploadStatus.uploadId)
            .multipartUpload(multipartUpload)
            .build()

        return s3Client.completeMultipartUpload(request).await()
    }

    private suspend fun initializeUpload(fileKey: String, file: FilePart): UploadStatus {
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val createResponse = s3Client.createMultipartUpload(
            CreateMultipartUploadRequest.builder()
                .contentType(contentType.toString())
                .metadata(mapOf("filename" to file.filename()))
                .key(fileKey)
                .bucket(bucketName)
                .build()
        ).await()

        AwsSdkUtils.checkSdkResponse(createResponse)
        return UploadStatus(
            contentType = contentType.toString(),
            fileKey = fileKey,
            uploadId = createResponse.uploadId(),
            buffered = 0
        )
    }

    private suspend fun uploadPart(uploadStatus: UploadStatus, partNumber: Int, data: ByteArray): CompletedPart {
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

        return CompletedPart.builder()
            .partNumber(partNumber)
            .eTag(response.eTag())
            .build()
    }

    suspend fun uploadSinglePart(fileKey: String, file: FilePart, contentLength: Long): FileResponse {
        log.info("[SinglePartUploader.upload] uploading file($fileKey, ${file.filename()}) to S3...")
        s3Client.createBucket { it.bucket(bucketName) }.await() // create bucket if not exists
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentLength(contentLength)
            .acl("public-read")
            .build()

        val byteArray = file.content().asFlow()
            .map { db ->
                db.toByteArray().also { DataBufferUtils.release(db) }
            }.toList().combine()

        val asyncBody = AsyncRequestBody.fromBytes(byteArray)

        val response = s3Client.putObject(request, asyncBody).await()

        AwsSdkUtils.checkSdkResponse(response)

        return FileResponse(
            name = file.filename(),
            uploadId = "",
            path = "$localEndpoint/$bucketName/$fileKey",
            type = "",
            eTag = response.eTag()
        )
    }
}