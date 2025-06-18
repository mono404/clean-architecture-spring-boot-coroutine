package com.mono.backend.s3client.uploader

import com.mono.backend.log.logger
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.util.AwsSdkUtils
import com.mono.backend.s3client.util.FileUtils.combine
import com.mono.backend.s3client.util.FileUtils.toByteArray
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class SinglePartUploader(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
) {
    private val log = logger()

    suspend fun upload(fileKey: String, file: FilePart, contentLength: Long): FileResponse {
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