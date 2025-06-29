package com.mono.backend.infra.s3Client.uploader

import com.mono.backend.common.log.logger
import com.mono.backend.infra.s3Client.config.UploadConfig
import com.mono.backend.infra.s3Client.retry.RetryEngine
import com.mono.backend.infra.s3Client.storage.UploadStorageProvider
import com.mono.backend.infra.s3Client.util.AwsSdkUtils
import com.mono.backend.infra.s3Client.util.FileUtils
import com.mono.backend.infra.s3Client.util.FileUtils.combine
import com.mono.backend.infra.s3Client.util.FileUtils.toByteArray
import com.mono.backend.port.infra.s3client.model.FileResponse
import com.mono.backend.port.infra.s3client.model.ProgressCallback
import com.mono.backend.port.infra.s3client.model.UploadStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Component
class S3Uploader(
    private val s3Client: S3AsyncClient,
    private val config: UploadConfig,
    private val resumeStorage: UploadStorageProvider,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) {
    private val log = logger()

    suspend fun upload(fileKey: String, file: FilePart, progressCallback: ProgressCallback?): FileResponse =
        coroutineScope {
            var uploadStatus: UploadStatus? = resumeStorage.load(fileKey)
            val completedParts = uploadStatus?.completeParts?.values?.toMutableList() ?: mutableListOf()
            val uploadedBytes = AtomicLong(uploadStatus?.uploadedSizes?.values?.sum() ?: 0L)
            val bufferList = mutableListOf<ByteArray>()
            var totalSize = uploadedBytes.get()
            var currentSize = 0L
            val deferredParts = mutableListOf<Deferred<CompletedPart>>()

            file.content().asFlow()
                .map { db -> db.toByteArray().also { DataBufferUtils.release(db) } }
                .collect { bytes ->
                    bufferList += bytes
                    totalSize += bytes.size
                    currentSize += bytes.size

                    if (uploadStatus == null && totalSize > config.multipartThreshold) {
                        uploadStatus = startMultipart(fileKey, file)
                    }

                    if (uploadStatus != null && currentSize >= config.multipartThreshold) {
                        val partData = bufferList.combine()
                        bufferList.clear()
                        currentSize = 0
                        deferredParts += uploadPartAsync(
                            partData,
                            uploadStatus!!,
                            completedParts,
                            uploadedBytes,
                            progressCallback
                        )
                    }
                }

            if (uploadStatus == null) {
                uploadSinglePart(fileKey, file, bufferList.combine())
            } else {
                if (bufferList.isNotEmpty()) {
                    deferredParts += uploadPartAsync(
                        bufferList.combine(),
                        uploadStatus!!,
                        completedParts,
                        uploadedBytes,
                        progressCallback
                    )
                }
                deferredParts.awaitAll()

                val completeResponse = uploadCompletePart(uploadStatus!!, fileKey)
                FileResponse(
                    name = file.filename(),
                    uploadId = uploadStatus!!.uploadId,
                    path = completeResponse.location(),
                    type = uploadStatus!!.contentType,
                    eTag = completeResponse.eTag()
                )
            }
        }

    private suspend fun startMultipart(fileKey: String, file: FilePart): UploadStatus {
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val response = RetryEngine.run {
            s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(contentType.toString())
                    .metadata(mapOf("filename" to file.filename()))
                    .build()
            ).await()
        }.also { AwsSdkUtils.checkSdkResponse(it) }

        return UploadStatus(
            contentType.toString(),
            fileKey,
            response.uploadId(),
            AtomicInteger(0)
        ).also { resumeStorage.save(it) }
    }

    private suspend fun uploadSinglePart(fileKey: String, file: FilePart, data: ByteArray): FileResponse {
        log.info("[SmartUploader.uploadSinglePart] uploading file($fileKey) / size : ${data.size} to S3...")
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentLength(data.size.toLong())
            .contentType(contentType.toString())
            .acl("public-read")
            .build()

        val response = RetryEngine.run {
            s3Client.putObject(request, AsyncRequestBody.fromBytes(data)).await()
        }.also { AwsSdkUtils.checkSdkResponse(it) }

        return FileResponse(
            file.filename(),
            "",
            "$localEndpoint/$bucketName/$fileKey",
            contentType.toString(),
            response.eTag()
        )
    }

    private fun CoroutineScope.uploadPartAsync(
        data: ByteArray,
        uploadStatus: UploadStatus,
        completedParts: MutableList<CompletedPart>,
        uploadedBytes: AtomicLong,
        progressCallback: ProgressCallback?
    ): Deferred<CompletedPart> = async {
        val partNumber = uploadStatus.getAddedPartCounter()
        val request = UploadPartRequest.builder()
            .bucket(bucketName)
            .key(uploadStatus.fileKey)
            .uploadId(uploadStatus.uploadId)
            .partNumber(partNumber)
            .contentLength(data.size.toLong())
            .build()

        val response = RetryEngine.run(maxRetries = 1) {
            log.info("[SmartUploader.uploadBatchParallel] uploading part(${request.partNumber()}), size : ${request.contentLength()}, uploadId : ${request.uploadId()} to S3...")
            s3Client.uploadPart(request, AsyncRequestBody.fromBytes(data)).await()
        }.also { AwsSdkUtils.checkSdkResponse(it) }

        val part = CompletedPart.builder().partNumber(partNumber).eTag(response.eTag()).build()
        synchronized(completedParts) { completedParts += part }
        uploadStatus.completeParts[partNumber] = part
        uploadStatus.uploadedSizes[partNumber] = data.size.toLong()
        uploadedBytes.addAndGet(data.size.toLong())
        progressCallback?.onProgress(uploadedBytes.get(), -1)
        resumeStorage.save(uploadStatus)
        part
    }

    private suspend fun uploadCompletePart(
        uploadStatus: UploadStatus,
        fileKey: String
    ): CompleteMultipartUploadResponse {
        log.info("[SmartUploader.uploadCompletePart] Complete upload fileKey=$fileKey, completedParts.size=${uploadStatus.completeParts.size}")
        val request = CompleteMultipartUploadRequest.builder()
            .bucket(bucketName)
            .uploadId(uploadStatus.uploadId)
            .multipartUpload(CompletedMultipartUpload.builder().parts(uploadStatus.completeParts.values).build())
            .key(uploadStatus.fileKey)
            .build()

        val completeResponse = RetryEngine.run {
            s3Client.completeMultipartUpload(request).await()
        }.also { AwsSdkUtils.checkSdkResponse(it) }

        resumeStorage.delete(fileKey)
        return completeResponse
    }

    fun uploadMultipartWebFlux(key: String, file: FilePart): Mono<FileResponse> {
        val contentType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val s3AsyncClientMultipartUpload =
            s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType(contentType.toString())
                    .metadata(mapOf("filename" to file.filename()))
                    .key(key)
                    .bucket(bucketName)
                    .build()
            )

        val uploadStatus = UploadStatus(file.headers().contentType.toString(), key)

        return Mono.fromFuture(s3AsyncClientMultipartUpload)
            .flatMapMany { response ->
                AwsSdkUtils.checkSdkResponse(response)
                uploadStatus.uploadId = response.uploadId()
                log.info("[S3FileStorage.store] Upload object with ID={}", response.uploadId())
                file.content()
            }
            .bufferUntil { dataBuffer ->
                uploadStatus.addBuffered(dataBuffer.readableByteCount())

                if (uploadStatus.buffered >= config.multipartPartSize) {
                    log.info(
                        "[S3FileStorage.store] BufferUntil - returning true, bufferedBytes={}, partCounter={}, uploadId={}",
                        uploadStatus.buffered,
                        uploadStatus.partCounter,
                        uploadStatus.uploadId
                    )
                    uploadStatus.buffered = 0
                    return@bufferUntil true
                }

                false
            }
            .map(FileUtils::dataBufferToByteBuffer)
            .flatMap { byteBuffer -> uploadPartObject(uploadStatus, byteBuffer) }
            .onBackpressureBuffer()
            .reduce(uploadStatus) { status, completedPart ->
                log.info("Completed: PartNumber={}, etag={}", completedPart.partNumber(), completedPart.eTag())
                status.completeParts[completedPart.partNumber()] = completedPart
                status
            }
            .flatMap { completeMultipartUpload(uploadStatus) }
            .map { response ->
                AwsSdkUtils.checkSdkResponse(response)
                log.info("upload result: {}", response.toString())
                FileResponse(
                    name = key,
                    uploadId = uploadStatus.uploadId,
                    path = response.location(),
                    type = uploadStatus.contentType,
                    eTag = response.eTag()
                )
            }
    }

    private fun completeMultipartUpload(uploadStatus: UploadStatus): Mono<CompleteMultipartUploadResponse> {
        log.info(
            "CompleteUpload - fileKey={}, completedParts.size={}",
            uploadStatus.fileKey,
            uploadStatus.completeParts.size
        )

        val multipartUpload = CompletedMultipartUpload.builder()
            .parts(uploadStatus.completeParts.values)
            .build()
        val request = CompleteMultipartUploadRequest.builder()
            .bucket(bucketName)
            .uploadId(uploadStatus.uploadId)
            .multipartUpload(multipartUpload)
            .key(uploadStatus.fileKey)
            .build()

        return Mono.fromFuture(
            s3Client.completeMultipartUpload(request)
        )
    }

    private fun uploadPartObject(uploadStatus: UploadStatus, buffer: ByteBuffer): Mono<CompletedPart> {
        val partNumber = uploadStatus.getAddedPartCounter()
        log.info("UploadPart - partNumber={}, contentLength={}", partNumber, buffer.capacity())

        val uploadPartResponseCompletableFuture = s3Client.uploadPart(
            UploadPartRequest.builder()
                .bucket(bucketName)
                .key(uploadStatus.fileKey)
                .partNumber(partNumber)
                .uploadId(uploadStatus.uploadId)
                .contentLength(buffer.capacity().toLong())
                .build(),
            AsyncRequestBody.fromPublisher(Mono.just(buffer))
        )

        return Mono.fromFuture(uploadPartResponseCompletableFuture)
            .map { uploadPartResult ->
                AwsSdkUtils.checkSdkResponse(uploadPartResult)
                log.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag())
                CompletedPart.builder()
                    .eTag(uploadPartResult.eTag())
                    .partNumber(partNumber)
                    .build()
            }
    }
}
