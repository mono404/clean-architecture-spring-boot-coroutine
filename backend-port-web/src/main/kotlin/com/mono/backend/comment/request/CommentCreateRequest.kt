package com.mono.backend.comment.request

import com.mono.backend.comment.Comment

data class CommentCreateRequest(
    val articleId: Long,
    val content: String,
    val parentCommentId: Long?,
    val writerId: Long
) {
    fun toDomain(commentId: Long, parent: Comment?): Comment {
        return Comment(
            commentId = commentId,
            content = content,
            parentCommentId = parent?.parentCommentId,
            articleId = articleId,
            writerId = writerId
        )
    }
}
