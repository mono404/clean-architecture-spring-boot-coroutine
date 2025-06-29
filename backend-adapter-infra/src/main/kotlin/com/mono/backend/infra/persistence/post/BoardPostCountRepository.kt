package com.mono.backend.infra.persistence.post

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardPostCountRepository : CoroutineCrudRepository<BoardPostCountEntity, Long> {
    @Modifying
    @Query(
        value = """
            UPDATE board_post_count 
            SET post_count = post_count + 1 
            WHERE board_id = :boardId
        """
    )
    suspend fun increase(boardId: Long): Int

    @Modifying
    @Query(
        value = """
            UPDATE board_post_count 
            SET post_count = post_count - 1 
            WHERE board_id = :boardId
        """
    )
    suspend fun decrease(boardId: Long): Int
}