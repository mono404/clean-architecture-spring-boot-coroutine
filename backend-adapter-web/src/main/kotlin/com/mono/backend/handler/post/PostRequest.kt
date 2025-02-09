package com.mono.backend.handler.post

import com.mono.backend.post.Post

data class PostRequest(
    val title: String,
    val content: String
) {
    companion object {
        fun PostRequest.toPost(): Post {
            return Post(
                title = this.title,
                content = this.content
            )
        }
    }
}