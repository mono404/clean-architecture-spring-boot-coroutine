package com.mono.backend.cache.hotpost

import java.time.Duration
import java.time.LocalDateTime

interface HotPostCreatedTimeCachePort {
    suspend fun createOrUpdate(postId: Long?, createdAt: LocalDateTime?, ttl: Duration)
    suspend fun delete(postId: Long)
    suspend fun read(postId: Long?): LocalDateTime?
}