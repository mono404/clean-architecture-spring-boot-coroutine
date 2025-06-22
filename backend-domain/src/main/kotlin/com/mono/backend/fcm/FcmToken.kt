package com.mono.backend.fcm

import java.time.Instant

data class FcmToken(
    val memberId: Long,
    val fcmToken: String,
    val deviceId: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)
