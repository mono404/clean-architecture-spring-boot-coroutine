package com.mono.backend.persistence.view

import com.mono.backend.view.ArticleViewCount
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountBackupPersistenceAdapter(
    private val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository
) {
    suspend fun save(articleViewCount: ArticleViewCount): ArticleViewCount {
        return articleViewCountBackUpRepository.save(ArticleViewCountEntity.from(articleViewCount)).toDomain()
    }

    suspend fun findById(articleId: Long): ArticleViewCount? {
        return articleViewCountBackUpRepository.findById(articleId)?.toDomain()
    }

    suspend fun updateViewCount(articleId: Long, viewCount: Long): Int {
        return articleViewCountBackUpRepository.updateViewCount(articleId, viewCount)
    }
}