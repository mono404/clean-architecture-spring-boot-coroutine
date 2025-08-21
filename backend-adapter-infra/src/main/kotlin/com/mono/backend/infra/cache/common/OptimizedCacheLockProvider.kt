package com.mono.backend.infra.cache.common

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider(
    private val redisTemplate: ReactiveStringRedisTemplate
) {
    companion object {
        private const val KEY_PREFIX = "optimized-cache-lock::"
        private val LOCK_TTL = Duration.ofSeconds(3)
    }

    suspend fun lock(key: String): Boolean? {
        return redisTemplate.opsForValue().setIfAbsent(
            generateLockKey(key),
            "",
            LOCK_TTL
        ).awaitSingleOrNull()
    }

    suspend fun unlock(key: String) {
        redisTemplate.delete(generateLockKey(key)).awaitSingle()
    }

    private fun generateLockKey(key: String): String {
        return KEY_PREFIX + key
    }
}