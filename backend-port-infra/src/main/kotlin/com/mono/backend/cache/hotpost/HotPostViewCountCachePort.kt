package com.mono.backend.cache.hotpost

import java.time.Duration

interface HotPostViewCountCachePort {
    suspend fun createOrUpdate(postId: Long, viewCount: Long, ttl: Duration)
    suspend fun delete(postId: Long)
    suspend fun read(postId: Long?): Long
}