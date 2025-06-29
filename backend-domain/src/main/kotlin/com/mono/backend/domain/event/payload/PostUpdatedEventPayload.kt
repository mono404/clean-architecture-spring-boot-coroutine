package com.mono.backend.domain.event.payload

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.domain.search.DomainType
import com.mono.backend.domain.search.SearchIndex
import java.time.LocalDateTime

data class PostUpdatedEventPayload(
    val postId: Long = 0,
    val title: String = "",
    val content: String = "",
    val boardType: BoardType = BoardType.FREE,
    val writerId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : EventPayload {
    companion object {
        fun from(post: Post) = PostUpdatedEventPayload(
            postId = post.postId,
            title = post.title,
            content = post.content,
            boardType = post.boardType,
            writerId = post.writerId,
            createdAt = post.createdAt!!,
            updatedAt = post.updatedAt!!
        )
    }

    override fun toSearchIndex() = SearchIndex(
        searchIndexId = postId,
        domainType = DomainType.POST,
        boardType = boardType,
        title = title,
        content = content,
    )
}
