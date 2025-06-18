package com.mono.backend.s3client.model

interface ProgressCallback {
    fun onProgress(uploadedBytes: Long, totalBytes: Long)
}