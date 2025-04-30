package com.mono.backend.persistence.like.count

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleLikeCountRepository : CoroutineCrudRepository<ArticleLikeCountEntity, Long> {

    //    @Lock(LockModeType.PESSIMISTIC_WRITE) // Locking is not supported by R2DBC yet.
    @Query(value = "SELECT * FROM article_like_count WHERE article_id = :articleId FOR UPDATE")
    suspend fun findLockedByArticleId(articleId: Long): ArticleLikeCountEntity?

    @Modifying
    @Query(
        value = """
            UPDATE article_like_count 
            SET like_count = like_count + 1 
            WHERE article_id = :articleId
        """
    )
    suspend fun increase(articleId: Long): Int

    @Modifying
    @Query(
        value = """
            UPDATE article_like_count 
            SET like_count = like_count - 1 
            WHERE article_id = :articleId
        """
    )
    suspend fun decrease(articleId: Long): Int
}