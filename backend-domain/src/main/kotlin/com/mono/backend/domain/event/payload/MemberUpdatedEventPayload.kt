package com.mono.backend.domain.event.payload

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.member.Member

class MemberUpdatedEventPayload(
    val memberId: Long = 0,
    val nickname: String = "",
    val profileImageUrl: String? = null
) : EventPayload {
    companion object {
        fun from(member: Member) = MemberUpdatedEventPayload(
            memberId = member.memberId,
            nickname = member.nickname,
            profileImageUrl = member.profileImageUrl,
        )
    }
}