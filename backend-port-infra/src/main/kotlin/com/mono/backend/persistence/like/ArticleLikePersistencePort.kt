package com.mono.backend.persistence.like

import com.mono.backend.like.ArticleLike

interface ArticleLikePersistencePort {
    suspend fun save(articleLike: ArticleLike): ArticleLike
    suspend fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike?
    suspend fun delete(articleLike: ArticleLike)
}