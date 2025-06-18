package com.mono.backend

import com.mono.backend.log.logger
import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.UploadStatus
import com.mono.backend.s3client.util.FileUtils
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.SdkResponse
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer

@Component
class S3FileStorage(
    private val s3Client: S3AsyncClient,
    private val uploadConfig: UploadConfig,
    @Value("\${aws.s3.bucketName}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) : FileStoragePort {
    private val log = logger()

    override suspend fun store(path: String, file: FilePart, fileSize: Long): String {
        s3Client.createBucket { it.bucket(bucketName) }.await() // create bucket if not exists
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(path)
            .contentLength(fileSize)
            .acl("public-read")
            .build()

        val publisher = file.content()
            .flatMapIterable { dataBuffer ->
                val buffers = mutableListOf<ByteBuffer>()

                dataBuffer.readableByteBuffers().use { iterator ->
                    while (iterator.hasNext()) {
                        buffers.add(iterator.next())
                    }
                }

                DataBufferUtils.release(dataBuffer)
                buffers
            }

        file.content()
            .bufferUntil { _ ->
                true
            }
            .map(FileUtils::dataBufferToByteBuffer)

        val asyncBody = AsyncRequestBody.fromPublisher(publisher)

        s3Client.putObject(request, asyncBody).await()

        return "$localEndpoint/$bucketName/$path"
    }

    fun storeV2(key: String, file: FilePart, fileSize: Long): Mono<FileResponse> {
        val filename = file.filename()

        val metadata = mapOf("filename" to filename)
        val mediaType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM

        val s3AsyncClientMultipartUpload = s3Client
            .createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType(mediaType.toString())
                    .metadata(metadata)
                    .key(key)
                    .bucket(bucketName)
                    .build()
            )

        val uploadStatus = UploadStatus(file.headers().contentType.toString(), key)

        return Mono.fromFuture(s3AsyncClientMultipartUpload)
            .flatMapMany { response ->
                checkSdkResponse(response)
                uploadStatus.uploadId = response.uploadId()
                log.info("[S3FileStorage.store] Upload object with ID={}", response.uploadId())
                file.content()
            }
            .bufferUntil { dataBuffer ->
                uploadStatus.addBuffered(dataBuffer.readableByteCount())

                if (uploadStatus.buffered >= uploadConfig.multipartPartSize) {
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
                checkSdkResponse(response)
                log.info("upload result: {}", response.toString())
                FileResponse(
                    name = filename,
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

        return Mono.fromFuture(
            s3Client.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .uploadId(uploadStatus.uploadId)
                    .multipartUpload(multipartUpload)
                    .key(uploadStatus.fileKey)
                    .build()
            )
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
                checkSdkResponse(uploadPartResult)
                log.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag())
                CompletedPart.builder()
                    .eTag(uploadPartResult.eTag())
                    .partNumber(partNumber)
                    .build()
            }
    }

    // FileUtil
    private fun checkSdkResponse(sdkResponse: SdkResponse) {
        if (isErrorSdkHttpResponse(sdkResponse)) {
            // TODO: UploadException
            throw RuntimeException(
                "${sdkResponse.sdkHttpResponse().statusCode()} - ${
                    sdkResponse.sdkHttpResponse().statusText()
                }"
            )
        }
    }

    // AwsSdkUtils
    private fun isErrorSdkHttpResponse(sdkResponse: SdkResponse): Boolean {
        return sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful
    }
}