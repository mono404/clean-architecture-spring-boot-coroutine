package com.mono.backend.fcm

data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String
)