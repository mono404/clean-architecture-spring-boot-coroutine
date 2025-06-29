package com.mono.backend.infra.persistence.fcm

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FcmTokenRepository: CoroutineCrudRepository<FcmTokenEntity, Long> {
    suspend fun deleteByMemberIdAndDeviceId(memberId: Long, deviceId: String): Boolean
    suspend fun findByMemberIdAndDeviceId(memberId: Long, deviceId: String): FcmTokenEntity?
    suspend fun findAllByMemberId(memberId: Long): MutableList<FcmTokenEntity>

    @Query("SELECT DISTINCT member_id FROM fcm_token")
    suspend fun findAllMemberIdsWithToken(): List<Long>
}