package com.mono.backend.persistence.refreshtoken

import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenPersistencePort {
    override suspend fun upsert(memberId: Long, deviceId: String, token: String, expiresAt: Instant) {
        refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)?.let { entity ->
            refreshTokenRepository.save(entity.copy(refreshToken = token, expiresAt = expiresAt))
        } ?: refreshTokenRepository.save(
            RefreshTokenEntity(
                refreshTokenId = Snowflake.nextId(),
                memberId = memberId,
                deviceId = deviceId,
                refreshToken = token,
                expiresAt = expiresAt,
            )
        )
    }

    override suspend fun find(memberId: Long, deviceId: String) =
        refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)?.refreshToken

    override suspend fun delete(memberId: Long) {
        refreshTokenRepository.deleteById(memberId)
    }

    override suspend fun delete(memberId: Long, deviceId: String) {
        refreshTokenRepository.deleteByMemberIdAndDeviceId(memberId, deviceId)
    }
}