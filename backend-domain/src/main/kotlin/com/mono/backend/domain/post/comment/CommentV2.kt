package com.mono.backend.domain.post.comment

import java.time.LocalDateTime

data class CommentV2(
    val commentId: Long,
    val content: String,
    val postId: Long,
    val writerId: Long,
    val commentPath: CommentPath,
    var deleted: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
) {
    fun isRoot(): Boolean = commentPath.isRoot()

    fun delete() {
        deleted = true
    }
}