package com.mono.backend.domain.post.like

import java.time.LocalDateTime

data class PostLike(
    val postLikeId: Long,
    val postId: Long,
    val memberId: Long,
    val createdAt: LocalDateTime? = null
) {
    companion object {
        fun from(postLikeId: Long, postId: Long, memberId: Long): PostLike {
            return PostLike(postLikeId, postId, memberId)
        }
    }
}