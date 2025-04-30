package com.mono.backend.cache.hotarticle

import java.time.Duration

interface HotArticleCommentCountCachePort {
    suspend fun createOrUpdate(articleId: Long?, commentCount: Long?, ttl: Duration)
    suspend fun delete(articleId: Long)
    suspend fun read(articleId: Long?): Long
}