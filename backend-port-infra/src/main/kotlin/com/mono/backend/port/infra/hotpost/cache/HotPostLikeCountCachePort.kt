package com.mono.backend.port.infra.hotpost.cache

import java.time.Duration

interface HotPostLikeCountCachePort {
    suspend fun createOrUpdate(postId: Long, likeCount: Long, ttl: Duration)
    suspend fun delete(postId: Long)
    suspend fun read(postId: Long?): Long
}