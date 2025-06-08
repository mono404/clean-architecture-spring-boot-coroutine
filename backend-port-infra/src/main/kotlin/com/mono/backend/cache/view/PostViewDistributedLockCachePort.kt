package com.mono.backend.cache.view

import java.time.Duration

interface PostViewDistributedLockCachePort {
    suspend fun lock(postId: Long, memberId: Long, ttl: Duration): Boolean?
}