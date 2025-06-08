package com.mono.backend.post.response

data class PostPageResponse(
    val posts: List<PostResponse>,
    val postCount: Long
)
