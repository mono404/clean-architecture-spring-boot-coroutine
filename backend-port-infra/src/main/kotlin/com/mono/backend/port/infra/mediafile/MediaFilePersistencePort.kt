package com.mono.backend.port.infra.mediafile

import com.mono.backend.domain.mediafile.MediaFile
import com.mono.backend.domain.mediafile.OwnerType

interface MediaFilePersistencePort {
    suspend fun save(mediaFile: MediaFile): MediaFile
    suspend fun findByOwnerId(ownerId: Long): List<MediaFile>
    suspend fun deleteByOwner(ownerId: Long)
    suspend fun deleteById(mediaFileId: Long)
    suspend fun saveAll(mediaFiles: List<MediaFile>): List<MediaFile>
    suspend fun findAllByOwner(ownerId: Long, ownerType: OwnerType): List<MediaFile>
    suspend fun deleteByOwner(ownerId: Long, ownerType: OwnerType)
}