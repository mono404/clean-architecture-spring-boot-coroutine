package com.mono.backend.cache.hotarticle

import java.time.Duration
import java.time.LocalDateTime

interface HotArticleListCachePort {
    suspend fun add(articleId: Long?, time: LocalDateTime?, score: Double, limit: Long, ttl: Duration)
    suspend fun remove(articleId: Long, time: LocalDateTime)
    suspend fun readAll(dateStr: String): List<Long?>?
}