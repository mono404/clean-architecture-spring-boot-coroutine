package com.mono.backend.fcm

import com.mono.backend.persistence.fcm.FcmTokenPersistencePort
import org.springframework.stereotype.Service

@Service
class FcmService(
    private val fcmTokenPersistencePort: FcmTokenPersistencePort
) : FcmUseCase {
    override suspend fun registerOrRefresh(memberId: Long, fcmToken: String, deviceId: String): FcmToken {
        val token = FcmToken(
            memberId = memberId,
            fcmToken = fcmToken,
            deviceId = deviceId
        )
        return fcmTokenPersistencePort.upsert(token)
    }

    override suspend fun delete(memberId: Long, deviceId: String): Boolean {
        return fcmTokenPersistencePort.delete(memberId, deviceId)
    }
}