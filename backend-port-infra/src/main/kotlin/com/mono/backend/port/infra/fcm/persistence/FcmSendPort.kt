package com.mono.backend.port.infra.fcm.persistence

import com.mono.backend.domain.fcm.FcmMessage

interface FcmSendPort {
    suspend fun sendToDevice(token: String, message: FcmMessage): String
    suspend fun sendToDevices(tokens: List<String>, message: FcmMessage): List<String>
    suspend fun sendToTopic(topic: String, message: FcmMessage): String
}