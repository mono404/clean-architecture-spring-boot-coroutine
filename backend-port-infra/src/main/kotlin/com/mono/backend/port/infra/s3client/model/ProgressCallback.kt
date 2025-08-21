package com.mono.backend.port.infra.s3client.model

fun interface ProgressCallback {
    fun onProgress(uploadedBytes: Long, totalBytes: Long)
}