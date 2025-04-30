package com.mono.backend.comment.response

import com.mono.backend.comment.CommentV2
import java.time.LocalDateTime

data class CommentResponseV2(
    val commentId: Long,
    val content: String,
    val articleId: Long,
    val writerId: Long,
    var deleted: Boolean,
    val path: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(comment: CommentV2): CommentResponseV2 {
            return CommentResponseV2(
                commentId = comment.commentId,
                content = comment.content,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                path = comment.commentPath,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!
            )
        }
    }
}
