package com.mono.backend.entity.post

import com.mono.backend.post.Post

data class PostEntity(
    val id: Int,
    val title: String,
    val content: String,
)

fun PostEntity.toPost(): Post {
    return Post(
        title = this.title,
        content = this.content
    )
}