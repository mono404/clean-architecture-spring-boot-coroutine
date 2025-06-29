package com.mono.backend.port.infra.post.persistence

import com.mono.backend.domain.post.board.BoardPostCount
import com.mono.backend.domain.post.board.BoardType

interface BoardPostCountPersistencePort {
    suspend fun save(boardPostCount: BoardPostCount): BoardPostCount
    suspend fun findById(boardType: BoardType): BoardPostCount?
    suspend fun increase(boardType: BoardType): Int
    suspend fun decrease(boardType: BoardType): Int
}