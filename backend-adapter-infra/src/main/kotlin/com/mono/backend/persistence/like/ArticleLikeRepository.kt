package com.mono.backend.persistence.like

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleLikeRepository : CoroutineCrudRepository<ArticleLikeEntity, Long> {
    suspend fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLikeEntity?
}