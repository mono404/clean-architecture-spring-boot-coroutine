package com.mono.backend.article

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.article.response.ArticleResponse

interface ArticleCommandUseCase {
    suspend fun create(request: ArticleCreateRequest): ArticleResponse
    suspend fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse
    suspend fun delete(articleId: Long)
    suspend fun count(boardId: Long): Long
}