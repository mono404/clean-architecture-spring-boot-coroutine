package com.mono.backend.post.response

data class PostReadPageResponse(
    val posts: List<PostReadResponse>,
    val postCount: Long
) {

}