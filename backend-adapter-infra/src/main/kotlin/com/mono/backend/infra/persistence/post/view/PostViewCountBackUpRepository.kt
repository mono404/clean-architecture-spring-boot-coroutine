package com.mono.backend.infra.persistence.post.view

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostViewCountBackUpRepository : CoroutineCrudRepository<PostViewCountEntity, Long> {

    @Modifying
    @Query(
        value = """
            UPDATE post_view_count SET view_count = :viewCount
            WHERE post_id = :postId AND view_count < :viewCount
        """
    )
    suspend fun updateViewCount(postId: Long, viewCount: Long): Int
}