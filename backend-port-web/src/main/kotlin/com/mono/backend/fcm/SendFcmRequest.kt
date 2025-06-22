package com.mono.backend.fcm

data class SendToDeviceRequest(
    val fcmToken: String,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)

data class SendMulticastRequest(
    val fcmTokens: List<String>,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)

data class SendTopicRequest(
    val topic: String,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)