package com.mono.backend.domain.event.payload

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.post.comment.CommentV2
import java.time.LocalDateTime

data class CommentCreatedEventPayload(
    val commentId: Long = 0,
    val content: String = "",
    val path: String = "",
    val postId: Long = 0,
    val writerId: Long = 0,
    val deleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val postCommentCount: Long = 0
) : EventPayload {
    companion object {
        fun from(commentV2: CommentV2, count: Long) = CommentCreatedEventPayload(
            commentId = commentV2.commentId,
            content = commentV2.content,
            postId = commentV2.postId,
            writerId = commentV2.writerId,
            deleted = commentV2.deleted,
            createdAt = commentV2.createdAt!!,
            updatedAt = commentV2.updatedAt!!,
            postCommentCount = count
        )
    }
}
