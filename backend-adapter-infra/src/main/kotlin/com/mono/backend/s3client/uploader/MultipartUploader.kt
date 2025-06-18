package com.mono.backend.s3client.uploader

import com.mono.backend.log.logger
import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.ProgressCallback
import com.mono.backend.s3client.model.UploadStatus
import com.mono.backend.s3client.storage.UploadStorageProvider
import com.mono.backend.s3client.util.AwsSdkUtils
import com.mono.backend.s3client.util.FileUtils.chunkedFlow
import com.mono.backend.s3client.util.FileUtils.combine
import com.mono.backend.s3client.util.FileUtils.toByteArray
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
class MultipartUploader(
    private val s3Client: S3AsyncClient,
    private val config: UploadConfig,
    private val resumeStorage: UploadStorageProvider,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) {
    private val log = logger()

    suspend fun upload(fileKey: String, file: FilePart, progressCallback: ProgressCallback?): FileResponse {
        log.info("[MultipartUploader.upload] uploading file($fileKey, ${file.filename()}) to S3...")
        s3Client.createBucket { it.bucket(bucketName) }.await() // create bucket if not exists
        val uploadStatus = initializeUpload(fileKey, file)
        val completedParts = mutableMapOf<Int, CompletedPart>()
        val uploadedBytes = AtomicLong(uploadStatus.buffered.toLong())

        val chunkedFlow = accumulateChunks(file)

        coroutineScope {
            chunkedFlow
                .chunkedFlow(config.maxParallelUploads)
                .collect { batch ->
                    val deferredList = batch.map { chunk ->
                        async { 
                            val partNumber = uploadStatus.getAddedPartCounter()
                            val part = uploadPart(uploadStatus, partNumber, chunk)
                            completedParts[partNumber] = part
                            uploadedBytes.addAndGet(chunk.size.toLong())
                            progressCallback?.onProgress(uploadedBytes.get(), -1)
                        }
                    }
                    deferredList.awaitAll()
                }
        }
        
        val completedList = completedParts.entries.sortedBy { it.key }.map { it.value }
        val completedResponse = completeMultipartUpload(uploadStatus, completedList)
        AwsSdkUtils.checkSdkResponse(completedResponse)

        resumeStorage.delete(uploadStatus.uploadId)

        return FileResponse(
            name = file.filename(),
            uploadId = uploadStatus.uploadId,
            path = completedResponse.location(),
            type = uploadStatus.contentType,
            eTag = completedResponse.eTag()
        )
    }

    private suspend fun completeMultipartUpload(
        uploadStatus: UploadStatus,
        parts: List<CompletedPart>
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

    private fun accumulateChunks(file: FilePart): Flow<ByteArray> = flow {
        val bufferList = mutableListOf<ByteArray>()
        var accumulatedSize = 0L

        file.content().asFlow()
            .map { dataBuffer ->
                dataBuffer.toByteArray().also { DataBufferUtils.release(dataBuffer) }
            }
            .collect { chunk ->
                bufferList.add(chunk)
                accumulatedSize += chunk.size

                if(accumulatedSize >= config.multipartPartSize) {
                    emit(bufferList.combine())
                    bufferList.clear()
                    accumulatedSize = 0
                }
            }
        if(bufferList.isNotEmpty()) {
            emit(bufferList.combine())
        }
    }

    private suspend fun initializeUpload(fileKey: String, file: FilePart): UploadStatus {
        val mediaType = file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM
        val createResponse = s3Client.createMultipartUpload(
            CreateMultipartUploadRequest.builder()
                .contentType(mediaType.toString())
                .metadata(mapOf("filename" to file.filename()))
                .key(fileKey)
                .bucket(bucketName)
                .build()
        ).await()

        AwsSdkUtils.checkSdkResponse(createResponse)
        return UploadStatus(
            contentType = mediaType.toString(),
            fileKey = fileKey,
            uploadId = createResponse.uploadId(),
            buffered = 0
        )
    }
}