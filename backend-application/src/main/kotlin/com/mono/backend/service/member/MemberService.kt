package com.mono.backend.service.member

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.auth.OAuthMemberInfo
import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.MemberRole
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.port.infra.member.persistence.MemberPersistencePort
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.member.dto.MemberResponse
import com.mono.backend.port.web.member.dto.UpdateProfileRequest
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberPersistencePort: MemberPersistencePort,
) : MemberUseCase {

    suspend fun join(provider: SocialProvider, providerId: String): Member {
        return memberPersistencePort.save(
            Member.create(
                memberId = Snowflake.nextId(),
                providerId = providerId,
                provider = provider,
                nickname = generateGuestNickname(),
                profileImageUrl = null,
                role = MemberRole.MEMBER
            )
        )
    }

    suspend fun findByOAuth(oauthInfo: OAuthMemberInfo) =
        memberPersistencePort.findByOAuth(oauthInfo.provider, oauthInfo.providerId)

    suspend fun findByOAuth(provider: SocialProvider, providerId: String) =
        memberPersistencePort.findByOAuth(provider, providerId)

    private fun generateGuestNickname(): String {
        val timeStamp = System.currentTimeMillis()
        val randomPart = (100..999).random()
        return "GUEST_${timeStamp}_${randomPart}"
    }

    override suspend fun updateProfile(memberId: Long, updateProfileRequest: UpdateProfileRequest) {
        val member = memberPersistencePort.findById(memberId) ?: throw IllegalArgumentException("member not found")
        val updated = member.updateProfile(updateProfileRequest.nickname, updateProfileRequest.profileImageUrl)
        memberPersistencePort.save(updated)
    }

    override suspend fun validateNickname(nickname: String): Boolean {
        if (!Member.isValidNicknameFormat(nickname)) return false
        return !memberPersistencePort.existsByNickname(nickname)
    }

    override suspend fun getEmbeddedMember(memberId: Long): EmbeddedMember {
        return memberPersistencePort.findById(memberId)?.toEmbeddedMember()
            ?: throw IllegalArgumentException("member not found")
    }

    override suspend fun getMember(memberId: Long): MemberResponse {
        val member = memberPersistencePort.findById(memberId)
            ?: throw IllegalArgumentException("member not found")
        return MemberResponse.from(member)
    }
}