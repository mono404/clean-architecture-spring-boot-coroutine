package com.mono.backend.domain.event.payload

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.domain.post.comment.CommentV2
import java.time.LocalDateTime

data class CommentDeletedEventPayload(
    val commentId: Long = 0,
    val content: String = "",
    val path: String = "",
    val postId: Long = 0,
    val deleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    // post comment count 반 정규화
    val postCommentCount: Long = 0,

    // member 반 정규화
    val memberId: Long = 0,
    val nickname: String = "",
    val profileImageUrl: String? = null,
) : EventPayload {
    companion object {
        fun from(commentV2: CommentV2, count: Long) = CommentDeletedEventPayload(
            commentId = commentV2.commentId,
            content = commentV2.content,
            postId = commentV2.postId,
            deleted = commentV2.deleted,
            createdAt = commentV2.createdAt!!,
            updatedAt = commentV2.updatedAt!!,

            postCommentCount = count,

            memberId = commentV2.member.memberId,
            nickname = commentV2.member.nickname,
            profileImageUrl = commentV2.member.profileImageUrl,
        )

        fun from(comment: Comment, count: Long) = CommentDeletedEventPayload(
            commentId = comment.commentId,
            content = comment.content,
            postId = comment.postId,
            deleted = comment.deleted,
            createdAt = comment.createdAt!!,
            updatedAt = comment.updatedAt!!,

            postCommentCount = count,

            memberId = comment.member.memberId,
            nickname = comment.member.nickname,
            profileImageUrl = comment.member.profileImageUrl,
        )
    }
}
