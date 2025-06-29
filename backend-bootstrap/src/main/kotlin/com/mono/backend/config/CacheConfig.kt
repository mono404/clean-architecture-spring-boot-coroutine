package com.mono.backend.config

import com.mono.backend.infra.cache.common.OptimizedCacheExecutor
import com.mono.backend.port.infra.common.cache.initCacheExecutor
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