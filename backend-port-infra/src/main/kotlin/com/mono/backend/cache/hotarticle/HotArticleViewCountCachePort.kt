package com.mono.backend.cache.hotarticle

import java.time.Duration

interface HotArticleViewCountCachePort {
    suspend fun createOrUpdate(articleId: Long, viewCount: Long, ttl: Duration)
    suspend fun delete(articleId: Long)
    suspend fun read(articleId: Long?): Long
}