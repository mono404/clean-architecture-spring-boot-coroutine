package com.mono.backend.cache.article

interface ArticleIdListCachePort {
    suspend fun readAll(boardId: Long, offset: Long, limit: Long): List<Long>?
    suspend fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, limit: Long): List<Long>?
    suspend fun add(boardId: Long, articleId: Long, limit: Long): Long?
    suspend fun delete(boardId: Long, articleId: Long): Long?
}