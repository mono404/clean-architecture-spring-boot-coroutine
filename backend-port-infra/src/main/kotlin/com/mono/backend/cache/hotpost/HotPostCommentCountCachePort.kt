package com.mono.backend.cache.hotpost

import java.time.Duration

interface HotPostCommentCountCachePort {
    suspend fun createOrUpdate(postId: Long?, commentCount: Long?, ttl: Duration)
    suspend fun delete(postId: Long)
    suspend fun read(postId: Long?): Long
}