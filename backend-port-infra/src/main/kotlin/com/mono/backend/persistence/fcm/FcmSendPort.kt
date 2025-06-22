package com.mono.backend.persistence.fcm

import com.mono.backend.fcm.FcmMessage

interface FcmSendPort {
    suspend fun sendToDevice(token: String, message: FcmMessage): String
    suspend fun sendToDevices(tokens: List<String>, message: FcmMessage): List<String>
    suspend fun sendToTopic(topic: String, message: FcmMessage): String
}