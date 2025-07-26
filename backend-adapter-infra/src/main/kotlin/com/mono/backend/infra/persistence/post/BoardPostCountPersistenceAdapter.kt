package com.mono.backend.infra.persistence.post

import com.mono.backend.domain.post.board.BoardPostCount
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.post.persistence.BoardPostCountPersistencePort
import org.springframework.stereotype.Repository

@Repository
class BoardPostCountPersistenceAdapter(
    private val boardPostCountRepository: BoardPostCountRepository
) : BoardPostCountPersistencePort {
    override suspend fun save(boardPostCount: BoardPostCount): BoardPostCount {
        return boardPostCountRepository.save(BoardPostCountEntity.from(boardPostCount)).toDomain()
    }

    override suspend fun findById(boardType: BoardType): BoardPostCount? {
        return boardPostCountRepository.findById(boardType.id)?.toDomain()
    }

    override suspend fun increase(boardType: BoardType): Int {
        return boardPostCountRepository.increase(boardType.id)
    }

    override suspend fun decrease(boardType: BoardType): Int {
        return boardPostCountRepository.decrease(boardType.id)
    }

    override suspend fun upsertIncrease(boardType: BoardType): Int {
        return boardPostCountRepository.upsertIncrease(boardType.id)
    }
}