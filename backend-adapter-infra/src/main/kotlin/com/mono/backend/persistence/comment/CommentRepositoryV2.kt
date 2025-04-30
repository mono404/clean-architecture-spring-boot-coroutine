package com.mono.backend.persistence.comment

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepositoryV2 : CoroutineCrudRepository<CommentV2Entity, Long> {
    @Query("SELECT * FROM comment_v2 WHERE path = :path")
    suspend fun findByPath(@Param("path") path: String): CommentV2Entity?

    @Query(
        value = """
            SELECT path FROM comment_v2 
            WHERE article_id = :articleId AND path > :pathPrefix AND path LIKE CONCAT(:pathPrefix, '%') 
            ORDER BY path DESC LIMIT 1
        """
    )
    suspend fun findDescendantsTopPath(
        @Param("articleId") articleId: Long,
        @Param("pathPrefix") pathPrefix: String
    ): String?

    @Query(
        value = """
            SELECT *
            FROM (
                SELECT comment_id as t_comment_id FROM comment_v2 
                WHERE article_id = :articleId 
                ORDER BY path ASC 
                LIMIT :limit OFFSET :offset 
            ) t LEFT JOIN comment_v2 ON t.t_comment_id = comment_v2.comment_id
        """
    )
    suspend fun findAll(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<CommentV2Entity>

    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT comment_id FROM comment_v2 WHERE article_id = :articleId LIMIT :limit 
            ) t
        """
    )
    suspend fun count(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): Long

    @Query(
        value = """
            SELECT * FROM comment_v2 
            WHERE article_id = :articleId 
            ORDER BY path ASC 
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long
    ): List<CommentV2Entity>

    @Query(
        value = """
            SELECT * FROM comment_v2 
            WHERE article_id = :articleId AND path > :lastPath 
            ORDER BY path ASC 
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("lastPath") lastPath: String,
        @Param("limit") limit: Long
    ): List<CommentV2Entity>
}