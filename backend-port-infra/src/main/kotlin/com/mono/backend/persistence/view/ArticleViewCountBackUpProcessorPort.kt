package com.mono.backend.persistence.view

interface ArticleViewCountBackUpProcessorPort {
    suspend fun backup(articleId: Long, viewCount: Long)
}