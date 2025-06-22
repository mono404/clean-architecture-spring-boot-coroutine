package com.mono.backend.fcm

import com.mono.backend.persistence.fcm.FcmSendPort
import com.mono.backend.persistence.fcm.FcmTokenPersistencePort
import org.springframework.stereotype.Service

@Service
class SendFcmService(
    private val fcmTokenPersistencePort: FcmTokenPersistencePort,
    private val fcmSendPort: FcmSendPort
): SendFcmUseCase {
    override suspend fun sendToMember(memberId: Long, message: FcmMessage): List<String> {
        val tokens = fcmTokenPersistencePort.findByMemberId(memberId).map { it.fcmToken }
        if(tokens.isEmpty()) throw IllegalArgumentException("No FCM tokens found for memberId: $memberId")
        return fcmSendPort.sendToDevices(tokens, message)
    }

    override suspend fun sendToDevice(fcmToken: String, message: FcmMessage): String {
        return fcmSendPort.sendToDevice(fcmToken, message)
    }

    override suspend fun sendToMulticast(fcmTokens: List<String>, message: FcmMessage): List<String> {
        return fcmSendPort.sendToDevices(fcmTokens, message)
    }

    override suspend fun sendToTopic(topic: String, message: FcmMessage): String {
        return fcmSendPort.sendToTopic(topic, message)
    }
}