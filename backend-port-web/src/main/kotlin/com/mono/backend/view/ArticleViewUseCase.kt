package com.mono.backend.view

interface ArticleViewUseCase {
    suspend fun increase(articleId: Long, userId: Long): Long?
    suspend fun count(articleId: Long): Long
}