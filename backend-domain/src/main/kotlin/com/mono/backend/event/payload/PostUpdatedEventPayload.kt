package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import com.mono.backend.post.Post
import java.time.LocalDateTime

data class PostUpdatedEventPayload(
    val postId: Long = 0,
    val title: String = "",
    val content: String = "",
    val boardId: Long = 0,
    val writerId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : EventPayload {
    companion object {
        fun from(post: Post) = PostUpdatedEventPayload(
            postId = post.postId,
            title = post.title,
            content = post.content,
            boardId = post.boardId,
            writerId = post.writerId,
            createdAt = post.createdAt!!,
            updatedAt = post.updatedAt!!
        )
    }
}
