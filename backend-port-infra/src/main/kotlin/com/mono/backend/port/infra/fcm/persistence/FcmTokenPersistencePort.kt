package com.mono.backend.port.infra.fcm.persistence

import com.mono.backend.domain.fcm.FcmToken

interface FcmTokenPersistencePort {
    suspend fun upsert(fcmToken: FcmToken): FcmToken
    suspend fun delete(memberId: Long, deviceId: String): Boolean
    suspend fun findAllByMemberId(memberId: Long): List<FcmToken>
    suspend fun findAllMemberIdsWithToken(): List<Long>
}