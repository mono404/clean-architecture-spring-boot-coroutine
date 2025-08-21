package com.mono.backend.port.infra.view.cache

interface PostViewCountCachePort {
    suspend fun read(postId: Long): Long
    suspend fun increase(postId: Long): Long?
}