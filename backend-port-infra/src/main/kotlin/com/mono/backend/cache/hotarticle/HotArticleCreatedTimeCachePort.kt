package com.mono.backend.cache.hotarticle

import java.time.Duration
import java.time.LocalDateTime

interface HotArticleCreatedTimeCachePort {
    suspend fun createOrUpdate(articleId: Long?, createdAt: LocalDateTime?, ttl: Duration)
    suspend fun delete(articleId: Long)
    suspend fun read(articleId: Long?): LocalDateTime?
}