package com.mono.backend.article.request

data class ArticleUpdateRequest(
    val title: String,
    val content: String,
)