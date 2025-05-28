package com.mono.backend.member

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
