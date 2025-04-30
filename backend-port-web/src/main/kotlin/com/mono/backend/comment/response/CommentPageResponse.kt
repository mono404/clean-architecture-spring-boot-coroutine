package com.mono.backend.comment.response

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long
)