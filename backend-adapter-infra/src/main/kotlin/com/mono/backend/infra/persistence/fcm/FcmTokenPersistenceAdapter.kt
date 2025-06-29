package com.mono.backend.infra.persistence.fcm

import com.mono.backend.domain.fcm.FcmToken
import com.mono.backend.port.infra.fcm.persistence.FcmTokenPersistencePort
import org.springframework.stereotype.Repository

@Repository
class FcmTokenPersistenceAdapter(
    private val fcmTokenRepository: FcmTokenRepository,
) : FcmTokenPersistencePort {

    override suspend fun upsert(fcmToken: FcmToken): FcmToken {
        return fcmTokenRepository.findByMemberIdAndDeviceId(fcmToken.memberId, fcmToken.deviceId)?.let { entity ->
            fcmTokenRepository.save(entity.copy(fcmToken = fcmToken.fcmToken)).toDomain()
        } ?: fcmTokenRepository.save(
            FcmTokenEntity.from(fcmToken)
        ).toDomain()
    }

    override suspend fun delete(memberId: Long, deviceId: String): Boolean {
        return fcmTokenRepository.deleteByMemberIdAndDeviceId(memberId, deviceId)
    }

    override suspend fun findAllByMemberId(memberId: Long): List<FcmToken> {
        return fcmTokenRepository.findAllByMemberId(memberId).map { it.toDomain() }
    }

    override suspend fun findAllMemberIdsWithToken(): List<Long> {
        return fcmTokenRepository.findAllMemberIdsWithToken()
    }
}