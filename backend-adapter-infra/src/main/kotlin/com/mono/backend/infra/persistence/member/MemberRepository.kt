package com.mono.backend.infra.persistence.member

import com.mono.backend.domain.member.SocialProvider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : CoroutineCrudRepository<MemberEntity, Long> {
    suspend fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): MemberEntity?
    suspend fun existsByNickname(nickname: String): Boolean
    suspend fun findAllByIdIn(id: List<Long>): List<MemberEntity>
    suspend fun findAllByMemberIdIn(memberId: List<Long>): List<MemberEntity>
}