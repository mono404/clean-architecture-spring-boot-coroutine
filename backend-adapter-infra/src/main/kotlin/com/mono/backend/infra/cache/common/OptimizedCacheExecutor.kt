package com.mono.backend.infra.cache.common

import com.mono.backend.infra.dataserializer.DataSerializer
import com.mono.backend.port.infra.common.cache.CacheExecutorPort
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class OptimizedCacheExecutor(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val optimizedCacheLockProvider: OptimizedCacheLockProvider
) : CacheExecutorPort {
    override suspend fun <T> execute(
        args: List<Any>,
        type: String,
        ttlSeconds: Long,
        clazz: Class<T>,
        function: suspend () -> T
    ): T {
        val key = generateKey(type, args)
        val cachedData = redisTemplate.opsForValue().get(key).awaitSingleOrNull()
            ?: return refresh(function, key, ttlSeconds)
        val optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache::class.java)
            ?: return refresh(function, key, ttlSeconds)

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(clazz)!!
        }

        if (optimizedCacheLockProvider.lock(key) == false) {
            return optimizedCache.parseData(clazz)!!
        }

        return try {
            refresh(function, key, ttlSeconds)
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }
    }

    private fun generateKey(prefix: String, args: List<Any>): String {
        return prefix + "::" + args.joinToString("::")
        // prefix =a, args = [1, 2]
        // == a::1::2
    }

    private suspend fun <T> refresh(function: suspend () -> T, key: String, ttlSeconds: Long): T {
        val result = function.invoke()

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)
        val optimizedCache = OptimizedCache.of(result as Any, optimizedCacheTTL.logicalTTL)

        DataSerializer.serialize(optimizedCache)?.let {
            redisTemplate.opsForValue().set(key, it, optimizedCacheTTL.physicalTTL).awaitSingle()
        }

        return result
    }
}