package com.mono.backend.comment

import java.time.LocalDateTime

data class CommentV2(
    val commentId: Long,
    val content: String,
    val articleId: Long,
    val writerId: Long,
    val commentPath: String,
    var deleted: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
) {
    fun isRoot(): Boolean = CommentPath(commentPath).isRoot()

    fun delete() {
        deleted = true
    }
}