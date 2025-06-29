package com.mono.backend.port.infra.s3client.model

data class FileResponse(
    val name: String,
    val uploadId: String,
    val path: String,
    val type: String,
    val eTag: String,
)
