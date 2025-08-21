package com.mono.backend.infra.cache.post.hot

import com.mono.backend.port.infra.hotpost.cache.HotPostLikeCountCachePort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class HotPostLikeCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotPostLikeCountCachePort {
    companion object {
        // hot-post::post::{postId}::like-count
        const val KEY_FORMAT = "hot-post::post::%s::like-count"
    }

    override suspend fun createOrUpdate(postId: Long, likeCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(postId), likeCount.toString(), ttl).awaitSingleOrNull()
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