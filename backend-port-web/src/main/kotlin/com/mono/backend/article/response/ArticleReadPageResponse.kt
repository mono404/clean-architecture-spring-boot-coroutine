package com.mono.backend.article.response

data class ArticleReadPageResponse(
    val articles: List<ArticleReadResponse>,
    val articleCount: Long
) {

}