package com.mono.backend.like.response

import com.mono.backend.like.PostLike
import java.time.LocalDateTime

data class PostLikeResponse(
    val postLikeId: Long,
    val postId: Long,
    val memberId: Long,
    val createAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(postLike: PostLike): PostLikeResponse = PostLikeResponse(
            postLikeId = postLike.postLikeId,
            postId = postLike.postId,
            memberId = postLike.memberId,
            createAt = postLike.createdAt!!
        )
    }
}
