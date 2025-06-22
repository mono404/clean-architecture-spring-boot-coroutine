package com.mono.backend.persistence.fcm

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FcmTokenRepository: CoroutineCrudRepository<FcmTokenEntity, Long> {
    suspend fun deleteByMemberIdAndDeviceId(memberId: Long, deviceId: String): Boolean
    suspend fun findByMemberIdAndDeviceId(memberId: Long, deviceId: String): FcmTokenEntity?
    fun findAllByMemberId(memberId: Long): MutableList<FcmTokenEntity>
}