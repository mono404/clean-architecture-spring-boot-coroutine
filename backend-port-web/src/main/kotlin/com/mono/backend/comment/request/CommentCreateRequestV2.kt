package com.mono.backend.comment.request

import com.mono.backend.comment.CommentPath
import com.mono.backend.comment.CommentV2

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
