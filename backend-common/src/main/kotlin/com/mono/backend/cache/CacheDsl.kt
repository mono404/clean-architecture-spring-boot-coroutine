package com.mono.backend.cache

import com.mono.backend.cache.common.CacheExecutorPort

lateinit var cacheExecutorPort: CacheExecutorPort

fun initCacheExecutor(executor: CacheExecutorPort) {
    cacheExecutorPort = executor
}

suspend inline fun <reified T> cache(
    args: List<Any>,
    type: String,
    ttlSeconds: Long,
    noinline function: suspend () -> T
): T {
    return cacheExecutorPort.execute(
        args = args,
        type = type,
        ttlSeconds = ttlSeconds,
        clazz = T::class.java, // 여기서 타입 넘겨줌
        function = function
    )
}