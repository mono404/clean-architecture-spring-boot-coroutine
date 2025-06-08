package com.mono.backend.post.request

data class PostUpdateRequest(
    val title: String,
    val content: String,
)