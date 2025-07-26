package com.mono.backend.infra.persistence.member

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.port.infra.member.persistence.MemberPersistencePort
import org.springframework.stereotype.Repository

@Repository
class MemberPersistenceAdapter(
    private val memberRepository: MemberRepository
) : MemberPersistencePort {
    override suspend fun save(member: Member): Member {
        return memberRepository.save(MemberEntity.from(member)).toDomain()
    }

    override suspend fun findById(memberId: Long): Member? {
        return memberRepository.findById(memberId)?.toDomain()
    }

    override suspend fun findByOAuth(provider: SocialProvider, providerId: String): Member? {
        return memberRepository.findByProviderAndProviderId(provider, providerId)?.toDomain()
    }

    override suspend fun existsByNickname(nickname: String): Boolean = memberRepository.existsByNickname(nickname)

    override suspend fun findAllByIdIn(memberIds: List<Long>): List<Member> {
        return memberRepository.findAllByMemberIdIn(memberIds).map { it.toDomain() }
    }
}