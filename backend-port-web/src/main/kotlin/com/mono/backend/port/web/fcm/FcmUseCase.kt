package com.mono.backend.port.web.fcm

import com.mono.backend.domain.fcm.FcmToken

interface FcmUseCase {
    suspend fun registerOrRefresh(memberId: Long, fcmToken: String, deviceId: String): FcmToken
    suspend fun delete(memberId: Long, deviceId: String): Boolean
}