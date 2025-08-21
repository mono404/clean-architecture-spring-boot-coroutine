package com.mono.backend.infra.persistence.mediafile

import com.mono.backend.domain.mediafile.MediaFile
import com.mono.backend.domain.mediafile.OwnerType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "media_file")
data class MediaFileEntity(
    @Id
    val mediaFileId: Long,
    val ownerType: Int,
    val ownerId: Long,
    val url: String,
    val mediaType: String,
    val size: Long,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long = mediaFileId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain() = MediaFile(
        mediaFileId = mediaFileId,
        ownerType = OwnerType.from(ownerType),
        ownerId = ownerId,
        s3Path = url,
        mediaType = mediaType,
        size = size,
        createdAt = createdAt
    )

    companion object {
        fun from(mediaFile: MediaFile) = MediaFileEntity(
            mediaFileId = mediaFile.mediaFileId,
            ownerType = mediaFile.ownerType.ownerTypeId,
            ownerId = mediaFile.ownerId,
            url = mediaFile.s3Path,
            mediaType = mediaFile.mediaType,
            size = mediaFile.size,
        )
    }
}

fun List<MediaFileEntity>.toDomain(): List<MediaFile> {
    return this.map { it.toDomain() }
}