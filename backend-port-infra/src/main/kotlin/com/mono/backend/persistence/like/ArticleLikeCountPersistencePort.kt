package com.mono.backend.persistence.like

import com.mono.backend.like.ArticleLikeCount

interface ArticleLikeCountPersistencePort {
    suspend fun save(articleLikeCount: ArticleLikeCount): ArticleLikeCount
    suspend fun findById(articleId: Long): ArticleLikeCount?
    suspend fun findLockedByArticleId(articleId: Long): ArticleLikeCount?
    suspend fun increase(articleId: Long): Int
    suspend fun decrease(articleId: Long): Int
}