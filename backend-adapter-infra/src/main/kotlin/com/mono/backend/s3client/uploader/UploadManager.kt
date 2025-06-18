package com.mono.backend.s3client.uploader

import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.ProgressCallback
import com.mono.backend.s3client.util.FileUtils.size
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component

@Component
class UploadManager(
    private val singlePartUploader: SinglePartUploader,
    private val multipartUploader: MultipartUploader,
    private val config: UploadConfig
) {
    suspend fun upload(fileKey: String, file: FilePart, progressCallback: ProgressCallback?): FileResponse {
        return if (file.size() <= config.multipartThreshold) {
            singlePartUploader.upload(fileKey, file, file.size())
        } else {
            multipartUploader.upload(fileKey, file, progressCallback)
        }
    }
}