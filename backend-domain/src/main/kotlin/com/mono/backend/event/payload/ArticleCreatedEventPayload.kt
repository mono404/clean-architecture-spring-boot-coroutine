package com.mono.backend.event.payload

import com.mono.backend.article.Article
import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class ArticleCreatedEventPayload(
    val articleId: Long = 0,
    val title: String = "",
    val content: String = "",
    val boardId: Long = 0,
    val writerId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val boardArticleCount: Long = 0,
): EventPayload {
    companion object {
        fun from(article: Article, count: Long) = ArticleCreatedEventPayload(
            articleId = article.articleId,
            title = article.title,
            content = article.content,
            boardId = article.boardId,
            writerId = article.writerId,
            createdAt = article.createdAt!!,
            updatedAt = article.updatedAt!!,
            boardArticleCount = count
        )
    }
}