package com.mono.backend.port.web.post.comment.dto

import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.domain.post.comment.CommentV2
import java.time.LocalDateTime

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long
)

data class CommentPageResponseV2(
    val comments: List<CommentResponseV2>,
    val commentCount: Long
)

data class CommentResponse(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val postId: Long,
    val writerId: Long,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                commentId = comment.commentId,
                content = comment.content,
                parentCommentId = comment.parentCommentId,
                postId = comment.postId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!
            )
        }
    }
}

data class CommentResponseV2(
    val commentId: Long,
    val content: String,
    val postId: Long,
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
                postId = comment.postId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                path = comment.commentPath.path,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!
            )
        }
    }
}