package com.mono.backend.infra.persistence.refreshtoken

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : CoroutineCrudRepository<RefreshTokenEntity, Long> {
    suspend fun deleteByMemberIdAndDeviceId(memberId: Long, deviceId: String): Int
    suspend fun findByMemberIdAndDeviceId(memberId: Long, deviceId: String): RefreshTokenEntity?
}