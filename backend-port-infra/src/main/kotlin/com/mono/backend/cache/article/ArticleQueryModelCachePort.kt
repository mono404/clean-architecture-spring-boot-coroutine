package com.mono.backend.cache.article

import com.mono.backend.article.ArticleQueryModel
import java.time.Duration

interface ArticleQueryModelCachePort {
    suspend fun read(articleId: Long): ArticleQueryModel?
    suspend fun create(articleQueryModel: ArticleQueryModel, ttl: Duration)
    suspend fun readAll(articleIds: List<Long>): List<ArticleQueryModel>?
    suspend fun delete(articleId: Long)
    suspend fun update(articleQueryModel: ArticleQueryModel)
}