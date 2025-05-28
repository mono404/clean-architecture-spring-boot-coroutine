package com.mono.backend.persistence.member

import com.mono.backend.member.Member
import com.mono.backend.member.SocialProvider

interface MemberPersistencePort {
    suspend fun save(member: Member): Member
    suspend fun findById(memberId: Long): Member?
    suspend fun findByOAuth(provider: SocialProvider, providerId: String): Member?
    suspend fun existsByNickname(nickname: String): Boolean
}