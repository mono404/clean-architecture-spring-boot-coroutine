package com.mono.backend.comment.response

data class CommentPageResponseV2(
    val comments: List<CommentResponseV2>,
    val commentCount: Long
)