package com.mono.backend.member

import com.mono.backend.auth.OAuthMemberInfo
import com.mono.backend.persistence.member.MemberPersistencePort
import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberPersistencePort: MemberPersistencePort,
) : MemberUseCase {

    suspend fun join(provider: SocialProvider, providerId: String): Member {
        return memberPersistencePort.save(
            Member(
                memberId = Snowflake.nextId(),
                providerId = providerId,
                provider = provider,
                nickname = generateGuestNickname(),
                profileImageUrl = null,
                role = MemberRole.MEMBER,
                createdAt = null,
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
        val updated = member.copy(
            nickname = updateProfileRequest.nickname,
            profileImageUrl = updateProfileRequest.profileImageUrl,
        )
        memberPersistencePort.save(updated)
    }

    override suspend fun validateNickname(nickname: String): Boolean {
        if(!Member.isValidNicknameFormat(nickname) ) return false
        return !memberPersistencePort.existsByNickname(nickname)
    }
}