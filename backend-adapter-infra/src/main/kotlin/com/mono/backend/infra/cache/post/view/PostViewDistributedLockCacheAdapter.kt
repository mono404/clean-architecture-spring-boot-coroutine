package com.mono.backend.infra.cache.post.view

import com.mono.backend.port.infra.view.cache.PostViewDistributedLockCachePort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class PostViewDistributedLockCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : PostViewDistributedLockCachePort {
    companion object {

        // view::post::{postId}::member::{memberId}::lock
        const val KET_FORMAT = "view::post::{postId}::member::{memberId}::lock"
    }

    override suspend fun lock(postId: Long, memberId: Long, ttl: Duration): Boolean? {
        val key = generateKey(postId, memberId)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl).awaitSingleOrNull()
    }

    private fun generateKey(postId: Long, memberId: Long): String {
        return String.format(KET_FORMAT, postId, memberId)
    }
}