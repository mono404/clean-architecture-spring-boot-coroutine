package com.mono.backend.domain.mediafile

import java.time.LocalDateTime

data class MediaFile(
    val mediaFileId: Long,
    val ownerType: OwnerType,
    val ownerId: Long,
    val s3Path: String,
    val mediaType: String,
    val size: Long,
    val createdAt: LocalDateTime? = null
)