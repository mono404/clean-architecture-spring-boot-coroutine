package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import com.mono.backend.like.PostLike
import java.time.LocalDateTime

data class PostLikedEventPayload(
    val postLikeId: Long = 0,
    val postId: Long = 0,
    val memberId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val postLikeCount: Long = 0
) : EventPayload {
    companion object {
        fun from(postLike: PostLike, count: Long) = PostLikedEventPayload(
            postLikeId = postLike.postLikeId,
            postId = postLike.postId,
            memberId = postLike.memberId,
            createdAt = postLike.createdAt!!,
            postLikeCount = count
        )
    }
}
