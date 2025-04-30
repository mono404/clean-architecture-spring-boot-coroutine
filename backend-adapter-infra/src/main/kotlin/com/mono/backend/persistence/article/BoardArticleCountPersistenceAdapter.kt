package com.mono.backend.persistence.article

import com.mono.backend.article.BoardArticleCount
import org.springframework.stereotype.Repository

@Repository
class BoardArticleCountPersistenceAdapter(
    private val boardArticleCountRepository: BoardArticleCountRepository
) : BoardArticleCountPersistencePort {
    override suspend fun save(boardArticleCount: BoardArticleCount): BoardArticleCount {
        return boardArticleCountRepository.save(BoardArticleCountEntity.from(boardArticleCount)).toDomain()
    }

    override suspend fun findById(boardId: Long): BoardArticleCount? {
        return boardArticleCountRepository.findById(boardId)?.toDomain()
    }

    override suspend fun increase(boardId: Long): Int {
        return boardArticleCountRepository.increase(boardId)
    }

    override suspend fun decrease(boardId: Long): Int {
        return boardArticleCountRepository.decrease(boardId)
    }
}