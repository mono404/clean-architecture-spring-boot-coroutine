package com.mono.backend.cache.hotarticle

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class HotArticleLikeCountCache(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotArticleLikeCountCachePort {
    companion object {
        // hot-article::article::{articleId}::like-count
        const val KEY_FORMAT = "hot-article::article::%s::like-count"
    }

    override suspend fun createOrUpdate(articleId: Long, likeCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), likeCount.toString(), ttl).awaitSingleOrNull()
    }

    override suspend fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId)).awaitSingleOrNull()
    }

    override suspend fun read(articleId: Long?): Long {
        return redisTemplate.opsForValue().get(generateKey(articleId)).awaitSingleOrNull()?.toLong() ?: 0L
    }

    private fun generateKey(articleId: Long?): String {
        return String.format(KEY_FORMAT, articleId)
    }
}