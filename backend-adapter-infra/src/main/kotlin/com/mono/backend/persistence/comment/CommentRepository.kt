package com.mono.backend.persistence.comment

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CoroutineCrudRepository<CommentEntity, Long> {
    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT comment_id FROM comment
                WHERE post_id = :postId AND parent_comment_id = :parentCommentId
                LIMIT :limit
            ) t
        """
    )
    suspend fun countBy(
        @Param("postId") postId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = """
            SELECT * FROM (
                SELECT comment_id as t_comment_id FROM comment 
                WHERE post_id = :postId 
                ORDER BY parent_comment_id asc, comment_id desc 
                LIMIT :limit OFFSET :offset
            ) t left join comment on t.t_comment_id = comment.comment_id
        """
    )
    suspend fun findAll(
        postId: Long,
        offset: Long,
        limit: Long,
    ): List<CommentEntity>

    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT comment_id FROM comment
                WHERE post_id = :postId
                LIMIT :limit
            ) t
        """
    )
    suspend fun count(
        postId: Long,
        limit: Long
    ): Long

    @Query(
        value = """
            SELECT * FROM comment
            WHERE post_id = :postId
            ORDER BY parent_comment_id asc, comment_id asc
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        postId: Long,
        limit: Long,
    ): List<CommentEntity>

    @Query(
        value = """
            SELECT * FROM comment
            WHERE post_id = :postId AND (
                parent_comment_id > :lastParentCommentId OR
                (parent_comment_id = :lastParentCommentId AND comment_id > :lastCommentId) 
            )
            ORDER BY parent_comment_id asc, comment_id asc
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        postId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long,
    ): List<CommentEntity>
}