package com.mono.backend.domain.search

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.post.board.BoardType
import java.time.LocalDateTime

data class SearchIndex(
    val searchIndexId: Long, // postId or TODO(campsiteId)
    val domainType: DomainType,
    val boardType: BoardType,
    val title: String? = null,
    val content: String? = null,
    val comment: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,

    // member 의 반정규화 필드
    val member: EmbeddedMember,
) {
    fun appendComment(newComment: String): SearchIndex {
        val combined = (this.comment.orEmpty() + " " + newComment).trim()
        // TODO : 3000자 제한 예시 (FTS 최적화)
        val updated = if (combined.length > 3000) combined.takeLast(3000) else combined
        return this.copy(comment = updated)
    }
}
