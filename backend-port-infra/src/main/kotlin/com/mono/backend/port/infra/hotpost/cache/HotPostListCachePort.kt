package com.mono.backend.port.infra.hotpost.cache

import java.time.Duration
import java.time.LocalDateTime

interface HotPostListCachePort {
    suspend fun add(postId: Long?, time: LocalDateTime?, score: Double, limit: Long, ttl: Duration)
    suspend fun remove(postId: Long, time: LocalDateTime)
    suspend fun readAll(dateStr: String): List<Long?>?
}