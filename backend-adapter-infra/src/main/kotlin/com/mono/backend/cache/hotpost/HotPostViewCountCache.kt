package com.mono.backend.cache.hotpost

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class HotPostViewCountCache(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotPostViewCountCachePort {
    companion object {
        // hot-post::post::{postId}::view-count
        const val KEY_FORMAT = "hot-post::post::%s::view-count"
    }

    override suspend fun createOrUpdate(postId: Long, viewCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(postId), viewCount.toString(), ttl).awaitSingleOrNull()
    }

    override suspend fun delete(postId: Long) {
        redisTemplate.delete(generateKey(postId)).awaitSingleOrNull()
    }

    override suspend fun read(postId: Long?): Long {
        return redisTemplate.opsForValue().get(generateKey(postId)).awaitSingleOrNull()?.toLong() ?: 0L
    }

    private fun generateKey(postId: Long?): String {
        return String.format(KEY_FORMAT, postId)
    }
}