package com.mono.backend.cache.hotarticle

import java.time.Duration

interface HotArticleLikeCountCachePort {
    suspend fun createOrUpdate(articleId: Long, likeCount: Long, ttl: Duration)
    suspend fun delete(articleId: Long)
    suspend fun read(articleId: Long?): Long
}