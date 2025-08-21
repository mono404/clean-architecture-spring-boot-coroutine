package com.mono.backend.port.infra.view.cache

import java.time.Duration

interface PostViewDistributedLockCachePort {
    suspend fun lock(postId: Long, memberId: Long, ttl: Duration): Boolean?
}