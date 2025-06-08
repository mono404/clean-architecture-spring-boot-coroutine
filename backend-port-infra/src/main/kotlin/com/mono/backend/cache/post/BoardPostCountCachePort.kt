package com.mono.backend.cache.post

interface BoardPostCountCachePort {
    suspend fun read(boardId: Long): Long?
    suspend fun createOrUpdate(boardId: Long, postCount: Long)
}