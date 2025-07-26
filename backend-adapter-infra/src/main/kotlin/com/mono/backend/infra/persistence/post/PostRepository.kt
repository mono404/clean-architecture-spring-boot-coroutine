package com.mono.backend.infra.persistence.post

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CoroutineCrudRepository<PostEntity, Long> {
    @Query(
        value = """
            SELECT * FROM (
                SELECT post_id as t_post_id FROM post
                WHERE board_id = :boardId
                ORDER BY post_id desc 
                LIMIT :limit OFFSET :offset
            ) t LEFT JOIN post on t.t_post_id = post.post_id
        """
    )
    suspend fun findAll(
        @Param("boardId") boardId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long
    ): List<PostEntity>

    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT post_id FROM post
                WHERE board_id = :boardId
                LIMIT :limit
            ) t
        """
    )
    suspend fun count(@Param("boardId") boardId: Long, @Param("limit") limit: Long): Long

    @Query(
        value = """
            SELECT * FROM post
            ORDER BY post_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(@Param("limit") limit: Long): List<PostEntity>

    @Query(
        value = """
            SELECT * FROM post
            WHERE post_id < :lastPostId
            ORDER BY post_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        @Param("limit") limit: Long,
        @Param("lastPostId") lastPostId: Long
    ): List<PostEntity>

    @Query(
        value = """
            SELECT * FROM post
            WHERE board_id = :boardId
            ORDER BY post_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScrollByBoard(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long,
    ): List<PostEntity>

    @Query(
        value = """
            SELECT * FROM post
            WHERE board_id = :boardId AND post_id < :lastPostId
            ORDER BY post_id desc LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScrollByBoard(
        @Param("boardId") boardId: Long,
        @Param("limit") limit: Long,
        @Param("lastPostId") lastPostId: Long,
    ): List<PostEntity>
}