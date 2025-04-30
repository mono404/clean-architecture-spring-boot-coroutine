package com.mono.backend.persistence.article

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : CoroutineCrudRepository<ArticleEntity, Long> {
    @Query(
        value = """
            SELECT * FROM (
                SELECT article_id as t_article_id FROM article
                WHERE board_id = :boardId
                ORDER BY article_id desc 
                LIMIT :limit OFFSET :offset
            ) t LEFT JOIN article on t.t_article_id = article.article_id
        """
    )
    suspend fun findAll(
        @Param("boardId") boardId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<ArticleEntity>

    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT article_id FROM article
                WHERE board_id = :boardId
                LIMIT :limit
            ) t
        """
    )
    suspend fun count(@Param("boardId") boardId: Long, @Param("limit") limit: Long): Long

    @Query(
        value = """
            SELECT * FROM article
            WHERE board_id = :boardId
            ORDER BY article_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long,
    ): List<ArticleEntity>

    @Query(
        value = """
            SELECT * FROM article
            WHERE board_id = :boardId AND article_id < :lastArticleId
            ORDER BY article_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long,
        @Param("lastArticleId") lastArticleId: Long,
    ): List<ArticleEntity>
}