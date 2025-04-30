package com.mono.backend.cache.common

import java.time.Duration

data class OptimizedCacheTTL(
    val logicalTTL: Duration,
    val physicalTTL: Duration,
) {
    companion object {
        const val PHYSICAL_TTL_DELAY_SECONDS = 5L

        fun of(ttlSeconds: Long): OptimizedCacheTTL {
            val logicalTTL = Duration.ofSeconds(ttlSeconds)
            val physicalTTL = logicalTTL.plusSeconds(PHYSICAL_TTL_DELAY_SECONDS)
            return OptimizedCacheTTL(logicalTTL, physicalTTL)
        }
    }
}