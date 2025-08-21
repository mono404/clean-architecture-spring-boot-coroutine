package com.mono.backend.domain.fcm

import java.time.LocalDateTime

data class FcmToken(
    val memberId: Long,
    val fcmToken: String,
    val deviceId: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
