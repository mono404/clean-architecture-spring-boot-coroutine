package com.mono.backend.port.web.post.hot.dto

import com.mono.backend.port.web.post.dto.PostReadResponse
import java.time.LocalDateTime

data class HotPostResponse(
    val postId: String,
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