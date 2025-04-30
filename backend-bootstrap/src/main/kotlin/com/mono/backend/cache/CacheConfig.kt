package com.mono.backend.cache

import com.mono.backend.cache.common.OptimizedCacheExecutor
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig(
    private val optimizedCacheExecutor: OptimizedCacheExecutor
) {
    @PostConstruct
    fun init() {
        initCacheExecutor(optimizedCacheExecutor)
    }
}