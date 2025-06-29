package com.mono.backend.port.web.post.comment.dto

import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.domain.post.comment.CommentPath
import com.mono.backend.domain.post.comment.CommentV2

data class CommentCreateRequest(
    val postId: Long,
    val content: String,
    val parentCommentId: Long?,
    val writerId: Long
) {
    fun toDomain(commentId: Long, parent: Comment?): Comment {
        return Comment(
            commentId = commentId,
            content = content,
            parentCommentId = parent?.parentCommentId,
            postId = postId,
            writerId = writerId
        )
    }
}

data class CommentCreateRequestV2(
    val postId: Long,
    val content: String,
    val parentPath: String?,
    val writerId: Long
) {
    fun toDomain(commentId: Long, parentCommentPath: CommentPath, descendantsTopPath: String?): CommentV2 {
        return CommentV2(
            commentId = commentId,
            content = content,
            postId = postId,
            writerId = writerId,
            commentPath = parentCommentPath.createChildCommentPath(descendantsTopPath),
        )
    }
}

data class CommentUpdateRequest(
    val content: String,
)