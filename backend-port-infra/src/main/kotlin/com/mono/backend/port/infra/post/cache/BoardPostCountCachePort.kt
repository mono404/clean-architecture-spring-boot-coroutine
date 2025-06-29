package com.mono.backend.port.infra.post.cache

import com.mono.backend.domain.post.board.BoardType

interface BoardPostCountCachePort {
    suspend fun read(boardType: BoardType): Long?
    suspend fun createOrUpdate(boardType: BoardType, postCount: Long)
}