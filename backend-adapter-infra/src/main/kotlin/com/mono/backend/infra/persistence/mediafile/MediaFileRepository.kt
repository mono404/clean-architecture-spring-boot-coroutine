package com.mono.backend.infra.persistence.mediafile

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaFileRepository : CoroutineCrudRepository<MediaFileEntity, Long> {
    suspend fun findByOwnerId(ownerId: Long): List<MediaFileEntity>
    suspend fun deleteByOwnerId(ownerId: Long)
    suspend fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: Int): List<MediaFileEntity>
    suspend fun deleteByOwnerIdAndOwnerType(ownerId: Long, ownerType: Int)
}