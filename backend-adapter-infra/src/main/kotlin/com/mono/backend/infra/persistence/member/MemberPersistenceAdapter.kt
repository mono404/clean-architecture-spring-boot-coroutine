package com.mono.backend.infra.persistence.member

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.port.infra.member.persistence.MemberPersistencePort
import org.springframework.stereotype.Repository

@Repository
class MemberPersistenceAdapter(
    private val memberRepository: MemberRepository
) : MemberPersistencePort {
    override suspend fun save(member: Member) = memberRepository.save(MemberEntity.from(member)).toDomain()

    override suspend fun findById(memberId: Long) = memberRepository.findById(memberId)?.toDomain()

    override suspend fun findByOAuth(provider: SocialProvider, providerId: String) =
        memberRepository.findByProviderAndProviderId(provider, providerId)?.toDomain()

    override suspend fun existsByNickname(nickname: String) = memberRepository.existsByNickname(nickname)
}