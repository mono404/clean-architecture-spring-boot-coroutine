package com.mono.backend.infra.s3Client.storage

import com.mono.backend.port.infra.s3client.model.UploadStatus
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryStorageProvider : UploadStorageProvider {
    private val store = ConcurrentHashMap<String, UploadStatus>()

    override suspend fun save(uploadStatus: UploadStatus) {
        store[uploadStatus.uploadId] = uploadStatus
    }

    override suspend fun load(uploadId: String): UploadStatus? = store[uploadId]

    override suspend fun delete(uploadId: String) {
        store.remove(uploadId)
    }
}