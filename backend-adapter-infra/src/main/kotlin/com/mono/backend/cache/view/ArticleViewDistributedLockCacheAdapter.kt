package com.mono.backend.cache.view

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewDistributedLockCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : ArticleViewDistributedLockCachePort {
    companion object {

        // view::article::{articleId}::user::{userId}::lock
        const val KET_FORMAT = "view::article::{articleId}::user::{userId}::lock"
    }

    override suspend fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean? {
        val key = generateKey(articleId, userId)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl).awaitSingleOrNull()
    }

    private fun generateKey(articleId: Long, userId: Long): String {
        return String.format(KET_FORMAT, articleId, userId)
    }
}