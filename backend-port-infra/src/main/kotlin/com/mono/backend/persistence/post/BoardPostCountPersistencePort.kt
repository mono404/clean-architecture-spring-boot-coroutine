package com.mono.backend.persistence.post

import com.mono.backend.post.BoardPostCount

interface BoardPostCountPersistencePort {
    suspend fun save(boardPostCount: BoardPostCount): BoardPostCount
    suspend fun findById(boardId: Long): BoardPostCount?
    suspend fun increase(boardId: Long): Int
    suspend fun decrease(boardId: Long): Int
}