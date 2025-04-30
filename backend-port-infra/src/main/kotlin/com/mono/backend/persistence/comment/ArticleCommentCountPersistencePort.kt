package com.mono.backend.persistence.comment

import com.mono.backend.comment.ArticleCommentCount

interface ArticleCommentCountPersistencePort {
    suspend fun save(articleCommentCount: ArticleCommentCount): ArticleCommentCount
    suspend fun findById(articleId: Long): ArticleCommentCount?
    suspend fun increase(articleId: Long): Int
    suspend fun decrease(articleId: Long): Int
}