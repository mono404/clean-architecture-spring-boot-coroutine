package com.mono.backend.persistence.post

import com.mono.backend.post.BoardPostCount
import org.springframework.stereotype.Repository

@Repository
class BoardPostCountPersistenceAdapter(
    private val boardPostCountRepository: BoardPostCountRepository
) : BoardPostCountPersistencePort {
    override suspend fun save(boardPostCount: BoardPostCount): BoardPostCount {
        return boardPostCountRepository.save(BoardPostCountEntity.from(boardPostCount)).toDomain()
    }

    override suspend fun findById(boardId: Long): BoardPostCount? {
        return boardPostCountRepository.findById(boardId)?.toDomain()
    }

    override suspend fun increase(boardId: Long): Int {
        return boardPostCountRepository.increase(boardId)
    }

    override suspend fun decrease(boardId: Long): Int {
        return boardPostCountRepository.decrease(boardId)
    }
}