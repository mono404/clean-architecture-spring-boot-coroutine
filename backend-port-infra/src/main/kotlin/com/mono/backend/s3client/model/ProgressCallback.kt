package com.mono.backend.s3client.model

fun interface ProgressCallback {
    fun onProgress(uploadedBytes: Long, totalBytes: Long)
}