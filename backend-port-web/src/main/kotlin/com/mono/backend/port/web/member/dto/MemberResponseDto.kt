package com.mono.backend.port.web.member.dto

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.MemberRole
import com.mono.backend.domain.member.SocialProvider

data class MemberResponse(
    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val role: MemberRole,
    val provider: SocialProvider
) {
    companion object {
        fun from(member: Member) = MemberResponse(
            memberId = member.memberId,
            nickname = member.nickname,
            profileImageUrl = member.profileImageUrl,
            role = member.role,
            provider = member.provider,
        )
    }
}
