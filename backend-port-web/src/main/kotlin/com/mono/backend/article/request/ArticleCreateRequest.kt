package com.mono.backend.article.request

import com.mono.backend.article.Article

data class ArticleCreateRequest(
    val title: String,
    val content: String,
    val writerId: Long,
    val boardId: Long
) {
    fun toDomain(articleId: Long): Article {
        return Article(
            articleId = articleId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId
        )
    }
}