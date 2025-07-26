package com.mono.backend.domain.common.member

/**
 * 반정규화용 멤버 정보
 */
data class EmbeddedMember(
    val memberId: Long, // 작성자 아이디
    val nickname: String,
    val profileImageUrl: String?
)
