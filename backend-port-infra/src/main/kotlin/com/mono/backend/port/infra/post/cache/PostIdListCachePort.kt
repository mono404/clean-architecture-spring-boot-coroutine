package com.mono.backend.port.infra.post.cache

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.board.BoardType

interface PostIdListCachePort {
    suspend fun readAll(boardType: BoardType, pageRequest: PageRequest): List<Long>?
    suspend fun readAllInfiniteScroll(boardType: BoardType, cursorRequest: CursorRequest): List<Long>?
    suspend fun save(boardType: BoardType, postId: Long, limit: Long = 1000L): Long?
    suspend fun saveAll(boardType: BoardType, postIds: List<Long>, limit: Long = 1000L)
    suspend fun delete(boardType: BoardType, postId: Long): Long?
}