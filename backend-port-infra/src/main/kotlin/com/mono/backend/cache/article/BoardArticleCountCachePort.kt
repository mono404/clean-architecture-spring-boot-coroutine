package com.mono.backend.cache.article

interface BoardArticleCountCachePort {
    suspend fun read(boardId: Long): Long?
    suspend fun createOrUpdate(boardId: Long, articleCount: Long)
}