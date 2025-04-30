package com.mono.backend.persistence.like

import com.mono.backend.like.ArticleLike
import org.springframework.stereotype.Repository

@Repository
class ArticleLikePersistenceAdapter(
    private val articleLikeRepository: ArticleLikeRepository
) : ArticleLikePersistencePort{
    override suspend fun save(articleLike: ArticleLike): ArticleLike {
        return articleLikeRepository.save(ArticleLikeEntity.from(articleLike)).toDomain()
    }

    override suspend fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike? {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)?.toDomain()
    }

    override suspend fun delete(articleLike: ArticleLike) {
        return articleLikeRepository.delete(ArticleLikeEntity.from(articleLike))
    }
}