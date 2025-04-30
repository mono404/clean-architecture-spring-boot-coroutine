package com.mono.backend.persistence.article

import com.mono.backend.article.BoardArticleCount

interface BoardArticleCountPersistencePort {
    suspend fun save(boardArticleCount: BoardArticleCount): BoardArticleCount
    suspend fun findById(boardId: Long): BoardArticleCount?
    suspend fun increase(boardId: Long): Int
    suspend fun decrease(boardId: Long): Int
}