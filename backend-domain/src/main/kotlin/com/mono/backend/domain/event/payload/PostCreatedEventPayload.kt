package com.mono.backend.domain.event.payload

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.domain.search.DomainType
import com.mono.backend.domain.search.SearchIndex
import java.time.LocalDateTime

data class PostCreatedEventPayload(
    val postId: Long = 0,
    val title: String = "",
    val content: String = "",
    val boardType: BoardType = BoardType.FREE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    // boardCount 반 정규화
    val boardPostCount: Long = 0,

    // member 반 정규화
    val memberId: Long = 0,
    val nickname: String = "",
    val profileImageUrl: String? = null,
) : EventPayload {
    companion object {
        fun from(post: Post, count: Long) = PostCreatedEventPayload(
            postId = post.postId,
            title = post.title,
            content = post.content,
            boardType = post.boardType,
            createdAt = post.createdAt!!,
            updatedAt = post.updatedAt!!,

            boardPostCount = count,

            memberId = post.member.memberId,
            nickname = post.member.nickname,
            profileImageUrl = post.member.profileImageUrl,
        )
    }

    override fun toSearchIndex() = SearchIndex(
        searchIndexId = postId,
        domainType = DomainType.POST,
        boardType = boardType,
        title = title,
        content = content,
        member = EmbeddedMember(
            memberId = memberId,
            nickname = nickname,
            profileImageUrl = profileImageUrl
        ),
    )
}