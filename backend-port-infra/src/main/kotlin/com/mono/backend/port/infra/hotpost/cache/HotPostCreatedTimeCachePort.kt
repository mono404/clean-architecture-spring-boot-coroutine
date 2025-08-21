package com.mono.backend.port.infra.hotpost.cache

import java.time.Duration
import java.time.LocalDateTime

interface HotPostCreatedTimeCachePort {
    suspend fun createOrUpdate(postId: Long?, createdAt: LocalDateTime?, ttl: Duration)
    suspend fun delete(postId: Long)
    suspend fun read(postId: Long?): LocalDateTime?
}