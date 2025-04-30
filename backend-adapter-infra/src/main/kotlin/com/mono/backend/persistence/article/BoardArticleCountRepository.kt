package com.mono.backend.persistence.article

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardArticleCountRepository : CoroutineCrudRepository<BoardArticleCountEntity, Long> {
    @Modifying
    @Query(
        value = """
            UPDATE board_article_count 
            SET article_count = article_count + 1 
            WHERE board_id = :boardId
        """
    )
    suspend fun increase(boardId: Long): Int

    @Modifying
    @Query(
        value = """
            UPDATE board_article_count 
            SET article_count = article_count - 1 
            WHERE board_id = :boardId
        """
    )
    suspend fun decrease(boardId: Long): Int
}