package com.mono.backend.domain.post.comment

import java.time.LocalDateTime

data class Comment(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val postId: Long,
    val writerId: Long,
    var deleted: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
) {
    constructor(commentId: Long, content: String, parentCommentId: Long?, postId: Long, writerId: Long) : this(
        commentId = commentId,
        content = content,
        parentCommentId = parentCommentId ?: commentId,
        postId = postId,
        writerId = writerId,
    )

    fun isRoot() = parentCommentId == commentId
    fun delete() {
        deleted = true
    }
}
