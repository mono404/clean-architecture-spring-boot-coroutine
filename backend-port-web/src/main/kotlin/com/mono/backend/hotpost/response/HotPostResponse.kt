package com.mono.backend.hotpost.response

import com.mono.backend.post.response.PostReadResponse
import java.time.LocalDateTime

data class HotPostResponse(
    val postId: Long,
    val title: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(postReadResponse: PostReadResponse) = HotPostResponse(
            postId = postReadResponse.postId,
            title = postReadResponse.title,
            createdAt = postReadResponse.createdAt
        )
    }
}
