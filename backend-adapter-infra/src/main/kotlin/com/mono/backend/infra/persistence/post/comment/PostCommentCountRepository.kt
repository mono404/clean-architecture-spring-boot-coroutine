package com.mono.backend.infra.persistence.post.comment

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostCommentCountRepository : CoroutineCrudRepository<PostCommentCountEntity, Long> {
    @Modifying
    @Query(
        value = "UPDATE post_comment_count SET comment_count = comment_count + 1 WHERE post_id = :postId"
    )
    suspend fun increase(postId: Long): Int

    @Modifying
    @Query(
        value = "UPDATE post_comment_count SET comment_count = comment_count - 1 WHERE post_id = :postId"
    )
    suspend fun decrease(postId: Long): Int
}