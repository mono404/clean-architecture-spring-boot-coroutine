package com.mono.backend.port.web.fcm

data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String
)