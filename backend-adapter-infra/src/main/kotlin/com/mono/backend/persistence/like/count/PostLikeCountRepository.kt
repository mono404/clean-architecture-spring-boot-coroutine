package com.mono.backend.persistence.like.count

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeCountRepository : CoroutineCrudRepository<PostLikeCountEntity, Long> {

    //    @Lock(LockModeType.PESSIMISTIC_WRITE) // Locking is not supported by R2DBC yet.
    @Query(value = "SELECT * FROM post_like_count WHERE post_id = :postId FOR UPDATE")
    suspend fun findLockedByPostId(postId: Long): PostLikeCountEntity?

    @Modifying
    @Query(
        value = """
            UPDATE post_like_count 
            SET like_count = like_count + 1 
            WHERE post_id = :postId
        """
    )
    suspend fun increase(postId: Long): Int

    @Modifying
    @Query(
        value = """
            UPDATE post_like_count 
            SET like_count = like_count - 1 
            WHERE post_id = :postId
        """
    )
    suspend fun decrease(postId: Long): Int
}