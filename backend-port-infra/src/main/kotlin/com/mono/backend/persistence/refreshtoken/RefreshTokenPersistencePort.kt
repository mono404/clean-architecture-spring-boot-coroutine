package com.mono.backend.persistence.refreshtoken

import java.time.Instant

interface RefreshTokenPersistencePort {
    suspend fun save(memberId: Long, deviceId: String, token: String, expiresAt: Instant)
    suspend fun upsert(memberId: Long, deviceId: String, token: String, expiresAt: Instant)
    suspend fun find(memberId: Long, deviceId: String): String?
    suspend fun delete(memberId: Long)
    suspend fun delete(memberId: Long, deviceId: String)
}