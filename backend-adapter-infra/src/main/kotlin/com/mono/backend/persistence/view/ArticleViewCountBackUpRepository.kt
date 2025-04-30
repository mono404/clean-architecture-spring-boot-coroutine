package com.mono.backend.persistence.view

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleViewCountBackUpRepository : CoroutineCrudRepository<ArticleViewCountEntity, Long> {

    @Modifying
    @Query(
        value = """
            UPDATE article_view_count SET view_count = :viewCount
            WHERE article_id = :articleId AND view_count < :viewCount
        """
    )
    suspend fun updateViewCount(articleId: Long, viewCount: Long): Int
}