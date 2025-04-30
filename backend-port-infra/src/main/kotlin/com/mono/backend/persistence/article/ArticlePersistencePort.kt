package com.mono.backend.persistence.article

import com.mono.backend.article.Article

interface ArticlePersistencePort {
    suspend fun save(article: Article): Article
    suspend fun findById(articleId: Long): Article?
    suspend fun findAll(boardId: Long, offset: Long, limit: Long): List<Article>
    suspend fun count(boardId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(boardId: Long, limit: Long): List<Article>
    suspend fun findAllInfiniteScroll(boardId: Long, limit: Long, lastArticleId: Long): List<Article>
    suspend fun delete(article: Article)
}