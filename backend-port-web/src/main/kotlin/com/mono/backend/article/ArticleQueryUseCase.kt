package com.mono.backend.article

import com.mono.backend.article.response.ArticleReadPageResponse
import com.mono.backend.article.response.ArticleReadResponse

interface ArticleQueryUseCase {
    suspend fun read(articleId: Long): ArticleReadResponse
    suspend fun readAll(boardId: Long, page: Long, pageSize: Long): ArticleReadPageResponse
    suspend fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, pageSize: Long): List<ArticleReadResponse>
    suspend fun count(boardId: Long): Long
}