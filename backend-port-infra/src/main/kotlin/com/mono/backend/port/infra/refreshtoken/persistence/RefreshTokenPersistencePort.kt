package com.mono.backend.port.infra.refreshtoken.persistence

import java.time.LocalDateTime

interface RefreshTokenPersistencePort {
    suspend fun upsert(memberId: Long, deviceId: String, token: String, expiresAt: LocalDateTime)
    suspend fun find(memberId: Long, deviceId: String): String?
    suspend fun delete(memberId: Long)
    suspend fun delete(memberId: Long, deviceId: String)
}