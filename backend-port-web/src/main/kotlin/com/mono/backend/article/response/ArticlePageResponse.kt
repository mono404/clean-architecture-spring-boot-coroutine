package com.mono.backend.article.response

data class ArticlePageResponse(
    val articles: List<ArticleResponse>,
    val articleCount: Long
)
