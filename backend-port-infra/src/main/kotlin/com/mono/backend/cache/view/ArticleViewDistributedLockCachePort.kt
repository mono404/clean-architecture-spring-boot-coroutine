package com.mono.backend.cache.view

import java.time.Duration

interface ArticleViewDistributedLockCachePort {
    suspend fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean?
}