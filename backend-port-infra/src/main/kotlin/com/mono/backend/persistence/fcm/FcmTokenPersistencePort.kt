package com.mono.backend.persistence.fcm

import com.mono.backend.fcm.FcmToken

interface FcmTokenPersistencePort {
    suspend fun upsert(fcmToken: FcmToken): FcmToken
    suspend fun delete(memberId: Long, deviceId: String): Boolean
    suspend fun findByMemberId(memberId: Long): List<FcmToken>
}