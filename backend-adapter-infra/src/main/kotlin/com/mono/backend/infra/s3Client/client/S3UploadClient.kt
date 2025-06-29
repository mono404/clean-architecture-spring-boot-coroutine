package com.mono.backend.infra.s3Client.client

import com.mono.backend.infra.s3Client.uploader.S3Uploader
import com.mono.backend.infra.s3Client.util.AwsSdkUtils
import com.mono.backend.port.infra.s3client.S3UploadClientPort
import com.mono.backend.port.infra.s3client.model.FileResponse
import com.mono.backend.port.infra.s3client.model.ProgressCallback
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest

@Component
class S3UploadClient(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    private val s3Uploader: S3Uploader
) : S3UploadClientPort {
    override suspend fun upload(
        fileKey: String,
        file: FilePart,
        progressCallback: ProgressCallback?
    ): FileResponse {
        return s3Uploader.upload(fileKey, file, progressCallback)
    }

    override suspend fun delete(fileKey: String) {
        val request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .build()
        s3Client.deleteObject(request).await()
            .also { AwsSdkUtils.checkSdkResponse(it) }
    }

    override suspend fun read(fileKey: String): FileResponse {
        val request = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .build()
        val response = s3Client.headObject(request).await()
            .also { AwsSdkUtils.checkSdkResponse(it) }

        return FileResponse(
            name = fileKey,
            uploadId = "",
            path = "https://$bucketName.s3.amazonaws.com/$fileKey",
            type = response.contentType(),
            eTag = response.eTag()
        )
    }

    override suspend fun update(
        fileKey: String,
        file: FilePart,
        progressCallback: ProgressCallback?
    ): FileResponse {
        delete(fileKey)
        return upload(fileKey, file, progressCallback)
    }
}