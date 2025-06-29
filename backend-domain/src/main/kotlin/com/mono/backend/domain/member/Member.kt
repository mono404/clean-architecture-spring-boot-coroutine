package com.mono.backend.domain.member

import java.time.LocalDateTime

data class Member(
    val memberId: Long,
    val providerId: String,
    val provider: SocialProvider,
    val nickname: String,
    val profileImageUrl: String?,
    val role: MemberRole,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun isValidNicknameFormat(nickname: String): Boolean {
            return nickname.length in 2..10
        }
    }
}