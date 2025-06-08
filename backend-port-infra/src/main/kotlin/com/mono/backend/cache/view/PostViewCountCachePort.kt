package com.mono.backend.cache.view

interface PostViewCountCachePort {
    suspend fun read(postId: Long): Long
    suspend fun increase(postId: Long): Long?
}