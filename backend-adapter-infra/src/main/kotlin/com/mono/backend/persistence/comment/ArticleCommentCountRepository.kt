package com.mono.backend.persistence.comment

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleCommentCountRepository : CoroutineCrudRepository<ArticleCommentCountEntity, Long> {
    @Modifying
    @Query(
        value = "UPDATE article_comment_count SET comment_count = comment_count + 1 WHERE article_id = :articleId"
    )
    suspend fun increase(articleId: Long): Int

    @Modifying
    @Query(
        value = "UPDATE article_comment_count SET comment_count = comment_count - 1 WHERE article_id = :articleId"
    )
    suspend fun decrease(articleId: Long): Int
}