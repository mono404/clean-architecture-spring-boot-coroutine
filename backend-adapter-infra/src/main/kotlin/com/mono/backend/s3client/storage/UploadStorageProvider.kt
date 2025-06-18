package com.mono.backend.s3client.storage

import com.mono.backend.s3client.model.UploadStatus

// TODO: RedisStorageProvider, MemoryStorageProvider 등으로 확장 필요
interface UploadStorageProvider {
    suspend fun save(uploadStatus: UploadStatus)
    suspend fun load(uploadId: String): UploadStatus?
    suspend fun delete(uploadId: String)
}