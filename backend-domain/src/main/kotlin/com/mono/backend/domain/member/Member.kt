package com.mono.backend.domain.member

import com.mono.backend.domain.common.member.EmbeddedMember
import java.time.LocalDateTime

data class Member(
    val memberId: Long,
    val providerId: String,
    val provider: SocialProvider,
    val nickname: String,
    val profileImageUrl: String?,
    val role: MemberRole,
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        private const val MIN_NICKNAME_LENGTH = 2
        private const val MAX_NICKNAME_LENGTH = 10

        fun create(
            memberId: Long,
            providerId: String,
            provider: SocialProvider,
            nickname: String,
            profileImageUrl: String?,
            role: MemberRole = MemberRole.MEMBER
        ): Member {
            validateNickname(nickname)

            return Member(
                memberId = memberId,
                providerId = providerId,
                provider = provider,
                nickname = nickname.trim(),
                profileImageUrl = profileImageUrl,
                role = role,
            )
        }
        
        fun isValidNicknameFormat(nickname: String): Boolean {
            return nickname.trim().length in MIN_NICKNAME_LENGTH..MAX_NICKNAME_LENGTH
        }

        private fun validateNickname(nickname: String) {
            require(isValidNicknameFormat(nickname)) {
                "닉네임은 ${MIN_NICKNAME_LENGTH}자 이상 ${MAX_NICKNAME_LENGTH}자 이하여야 합니다."
            }
        }
    }

    fun updateProfile(newNickname: String, newProfileImageUrl: String?): Member {
        validateNickname(newNickname)

        return copy(
            nickname = newNickname.trim(),
            profileImageUrl = newProfileImageUrl
        )
    }

    fun toEmbeddedMember(): EmbeddedMember {
        return EmbeddedMember(
            memberId = memberId,
            nickname = nickname,
            profileImageUrl = profileImageUrl
        )
    }

    fun hasRole(requiredRole: MemberRole): Boolean {
        return role == requiredRole || role == MemberRole.ADMIN
    }

    fun isAdmin(): Boolean {
        return role == MemberRole.ADMIN
    }
}