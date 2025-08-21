package com.mono.backend.infra.persistence.mediafile

import com.mono.backend.domain.mediafile.MediaFile
import com.mono.backend.domain.mediafile.OwnerType
import com.mono.backend.port.infra.mediafile.MediaFilePersistencePort
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Repository

@Repository
class MediaFilePersistenceAdapter(
    private val mediaFileRepository: MediaFileRepository
) : MediaFilePersistencePort {
    override suspend fun save(mediaFile: MediaFile): MediaFile {
        return mediaFileRepository.save(MediaFileEntity.from(mediaFile)).toDomain()
    }

    override suspend fun findByOwnerId(ownerId: Long): List<MediaFile> {
        return mediaFileRepository.findByOwnerId(ownerId).toDomain()
    }

    override suspend fun deleteByOwner(ownerId: Long) {
        return mediaFileRepository.deleteByOwnerId(ownerId)
    }

    override suspend fun deleteById(mediaFileId: Long) {
        return mediaFileRepository.deleteById(mediaFileId)
    }

    override suspend fun saveAll(mediaFiles: List<MediaFile>): List<MediaFile> {
        return mediaFileRepository.saveAll(mediaFiles.map { MediaFileEntity.from(it) }).toList().toDomain()
    }

    override suspend fun findAllByOwner(ownerId: Long, ownerType: OwnerType): List<MediaFile> {
        return mediaFileRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType.ownerTypeId).toDomain()
    }

    override suspend fun deleteByOwner(ownerId: Long, ownerType: OwnerType) {
        return mediaFileRepository.deleteByOwnerIdAndOwnerType(ownerId, ownerType.ownerTypeId)
    }
}