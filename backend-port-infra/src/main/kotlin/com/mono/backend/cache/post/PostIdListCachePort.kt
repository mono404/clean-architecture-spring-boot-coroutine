package com.mono.backend.cache.post

interface PostIdListCachePort {
    suspend fun readAll(boardId: Long, offset: Long, limit: Long): List<Long>?
    suspend fun readAllInfiniteScroll(boardId: Long, lastPostId: Long?, limit: Long): List<Long>?
    suspend fun add(boardId: Long, postId: Long, limit: Long): Long?
    suspend fun delete(boardId: Long, postId: Long): Long?
}