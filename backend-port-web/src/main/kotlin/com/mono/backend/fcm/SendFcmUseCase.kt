package com.mono.backend.fcm

interface SendFcmUseCase {
    suspend fun sendToMember(memberId: Long, message: FcmMessage): List<String>
    suspend fun sendToDevice(fcmToken: String, message: FcmMessage): String
    suspend fun sendToMulticast(fcmTokens: List<String>, message: FcmMessage): List<String>
    suspend fun sendToTopic(topic: String, message: FcmMessage): String
}