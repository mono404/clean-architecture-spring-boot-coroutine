package com.mono.backend.event.payload

import com.mono.backend.comment.CommentV2
import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class CommentDeletedEventPayload(
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
        fun from(commentV2: CommentV2, count: Long) = CommentDeletedEventPayload(
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
