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
    val commentId: String,
    val content: String,
    val parentCommentId: String,
    val postId: String,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    val memberId: String,
    val nickname: String,
    val profileImageUrl: String?,
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                commentId = comment.commentId.toString(),
                content = comment.content,
                parentCommentId = comment.parentCommentId.toString(),
                postId = comment.postId.toString(),
                deleted = comment.deleted,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!,

                memberId = comment.member.memberId.toString(),
                nickname = comment.member.nickname,
                profileImageUrl = comment.member.profileImageUrl,
            )
        }
    }
}

data class CommentResponseV2(
    val commentId: String,
    val content: String,
    val postId: String,
    var deleted: Boolean,
    val path: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    val memberId: String,
    val nickname: String,
    val profileImageUrl: String?,
) {
    companion object {
        fun from(comment: CommentV2): CommentResponseV2 {
            return CommentResponseV2(
                commentId = comment.commentId.toString(),
                content = comment.content,
                postId = comment.postId.toString(),
                deleted = comment.deleted,
                path = comment.commentPath.path,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!,

                memberId = comment.member.memberId.toString(),
                nickname = comment.member.nickname,
                profileImageUrl = comment.member.profileImageUrl,
            )
        }
    }
}