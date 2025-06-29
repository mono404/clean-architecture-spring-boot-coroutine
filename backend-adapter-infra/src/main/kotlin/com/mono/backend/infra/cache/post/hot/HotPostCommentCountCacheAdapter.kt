package com.mono.backend.infra.cache.post.hot

import com.mono.backend.port.infra.hotpost.cache.HotPostCommentCountCachePort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class HotPostCommentCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotPostCommentCountCachePort {
    companion object {
        // hot-post::post::{postId}::comment-count
        const val KEY_FORMAT = "hot-post::post::%s::comment-count"
    }

    override suspend fun createOrUpdate(postId: Long?, commentCount: Long?, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(postId), commentCount.toString(), ttl).awaitSingleOrNull()
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