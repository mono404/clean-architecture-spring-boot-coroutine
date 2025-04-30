package com.mono.backend.persistence.like.count

import com.mono.backend.like.ArticleLikeCount
import com.mono.backend.persistence.like.ArticleLikeCountPersistencePort
import org.springframework.stereotype.Repository

@Repository
class ArticleLikeCountPersistenceAdapter(
    private val articleLikeCountRepository: ArticleLikeCountRepository
): ArticleLikeCountPersistencePort {
    override suspend fun save(articleLikeCount: ArticleLikeCount): ArticleLikeCount {
        return articleLikeCountRepository.save(ArticleLikeCountEntity.from(articleLikeCount)).toDomain()
    }

    override suspend fun findById(articleId: Long): ArticleLikeCount? {
        return articleLikeCountRepository.findById(articleId)?.toDomain()
    }

    override suspend fun findLockedByArticleId(articleId: Long): ArticleLikeCount? {
        return articleLikeCountRepository.findLockedByArticleId(articleId)?.toDomain()
    }

    override suspend fun increase(articleId: Long): Int {
        return articleLikeCountRepository.increase(articleId)
    }

    override suspend fun decrease(articleId: Long): Int {
        return articleLikeCountRepository.decrease(articleId)
    }
}