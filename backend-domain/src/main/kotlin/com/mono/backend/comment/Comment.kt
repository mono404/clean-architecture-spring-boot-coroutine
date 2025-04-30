package com.mono.backend.comment

import java.time.LocalDateTime

data class Comment(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long,
    val writerId: Long,
    var deleted: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
) {
    constructor(commentId: Long, content: String, parentCommentId: Long?, articleId: Long, writerId: Long) : this(
        commentId = commentId,
        content = content,
        parentCommentId = parentCommentId ?: commentId,
        articleId = articleId,
        writerId = writerId,
    )

    fun isRoot() = parentCommentId == commentId
    fun delete() {
        deleted = true
    }
}
