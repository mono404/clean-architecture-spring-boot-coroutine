package com.mono.backend.infra.cache.post.view

import com.mono.backend.port.infra.view.cache.PostViewCountCachePort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class PostViewCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate,
) : PostViewCountCachePort {
    companion object {
        // view::post::{postId}::view_count
        const val KET_FORMAT = "view::post::%s::view_count"
    }

    override suspend fun read(postId: Long): Long = redisTemplate.opsForValue()
        .get(generateKey(postId))
        .awaitSingleOrNull()?.toLong()
        ?: 0

    override suspend fun increase(postId: Long): Long? {
        return redisTemplate.opsForValue().increment(generateKey(postId)).awaitSingleOrNull()
    }

    private fun generateKey(postId: Long): String {
        return String.format(KET_FORMAT, postId)
    }
}