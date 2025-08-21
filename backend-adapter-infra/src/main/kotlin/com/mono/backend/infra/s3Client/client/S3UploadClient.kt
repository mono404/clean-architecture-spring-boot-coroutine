package com.mono.backend.infra.s3Client.client

import com.mono.backend.common.log.logger
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
import software.amazon.awssdk.services.s3.model.*
import java.net.URI

@Component
class S3UploadClient(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    private val s3Uploader: S3Uploader
) : S3UploadClientPort {
    private val log = logger()
    override suspend fun upload(
        fileKey: String,
        file: FilePart,
        progressCallback: ProgressCallback?
    ): FileResponse {
        return s3Uploader.upload(fileKey, file, progressCallback)
    }

    override suspend fun delete(s3Url: String) {
        val key = extractS3Key(s3Url)
        val request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()
        s3Client.deleteObject(request).await()
            .also { AwsSdkUtils.checkSdkResponse(it) }
    }

    override suspend fun delete(s3Urls: List<String>) {
        val keys = s3Urls.map { extractS3Key(it) }

        val identifiers = keys.map { key ->
            ObjectIdentifier.builder()
                .key(key)
                .build()
        }

        val delete = Delete.builder()
            .objects(identifiers)
            .build()

        val deleteRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete(delete)
            .build()

        s3Client.deleteObjects(deleteRequest)
            .whenComplete { response, error ->
                if (error != null) {
                    log.error("Multi-delete failed: ${error.message}")
                } else {
                    log.info("Deleted: ${response.deleted().size} objects")
                }
            }
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

    private fun extractS3Key(s3Url: String): String {
        val uri = URI(s3Url)
        var path = uri.path.removePrefix("/")
        if (path.startsWith("$bucketName/")) {
            path = path.removePrefix("$bucketName/")
        }

        return path
    }
}