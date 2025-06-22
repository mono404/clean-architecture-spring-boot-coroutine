package com.mono.backend.fcm

interface FcmUseCase {
    suspend fun registerOrRefresh(memberId: Long, fcmToken: String, deviceId: String): FcmToken
    suspend fun delete(memberId: Long, deviceId: String): Boolean
}