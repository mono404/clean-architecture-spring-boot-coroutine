package com.mono.backend.infra.cache.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mono.backend.infra.dataserializer.DataSerializer
import java.time.Duration
import java.time.LocalDateTime

data class OptimizedCache(
    val data: String?,
    val expiredAt: LocalDateTime
) {
    companion object {
        // logical TTL
        fun of(data: Any, ttl: Duration): OptimizedCache {
            return OptimizedCache(
                data = DataSerializer.serialize(data),
                expiredAt = LocalDateTime.now().plus(ttl)
            )
        }
    }

    @JsonIgnore
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiredAt)
    }

    fun <T> parseData(dataType: Class<T>): T? {
        return DataSerializer.deserialize(data, dataType)
    }
}
