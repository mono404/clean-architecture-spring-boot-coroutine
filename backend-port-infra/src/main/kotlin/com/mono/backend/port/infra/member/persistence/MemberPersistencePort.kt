package com.mono.backend.port.infra.member.persistence

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.SocialProvider

interface MemberPersistencePort {
    suspend fun save(member: Member): Member
    suspend fun findById(memberId: Long): Member?
    suspend fun findByOAuth(provider: SocialProvider, providerId: String): Member?
    suspend fun existsByNickname(nickname: String): Boolean
    suspend fun findAllByIdIn(memberIds: List<Long>): List<Member>
}