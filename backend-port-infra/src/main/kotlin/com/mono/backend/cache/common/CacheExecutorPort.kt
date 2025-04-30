package com.mono.backend.cache.common

interface CacheExecutorPort {
    suspend fun <T> execute(
        args: List<Any>,
        type: String,
        ttlSeconds: Long,
        clazz: Class<T>,
        function: suspend () -> T
    ): T
}