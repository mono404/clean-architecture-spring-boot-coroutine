package com.mono.backend.cache.view

interface ArticleViewCountCachePort {
    suspend fun read(articleId: Long): Long
    suspend fun increase(articleId: Long): Long?
}