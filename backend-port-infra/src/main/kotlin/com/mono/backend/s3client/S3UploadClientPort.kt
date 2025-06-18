package com.mono.backend.s3client

import com.mono.backend.s3client.model.FileResponse
import com.mono.backend.s3client.model.ProgressCallback
import org.springframework.http.codec.multipart.FilePart

interface S3UploadClientPort {
    suspend fun upload(
        fileKey: String,
        file: FilePart,
        progressCallback: ProgressCallback? = null
    ): FileResponse

    suspend fun delete(fileKey: String)
    suspend fun read(fileKey: String): FileResponse
    suspend fun update(
        fileKey: String,
        file: FilePart,
        progressCallback: ProgressCallback? = null
    ): FileResponse
}