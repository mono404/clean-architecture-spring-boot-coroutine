package com.mono.backend.post.request

data class TmpRequest(
    val title: String,
    val content: List<String>,
    val boardId: Long,
)
