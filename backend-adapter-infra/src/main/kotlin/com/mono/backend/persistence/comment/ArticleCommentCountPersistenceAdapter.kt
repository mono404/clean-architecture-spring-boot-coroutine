package com.mono.backend.persistence.comment

import com.mono.backend.comment.ArticleCommentCount
import org.springframework.stereotype.Repository

@Repository
class ArticleCommentCountPersistenceAdapter(
    private val articleCommentCountRepository: ArticleCommentCountRepository
):ArticleCommentCountPersistencePort {
    override suspend fun save(articleCommentCount: ArticleCommentCount): ArticleCommentCount {
        return articleCommentCountRepository.save(ArticleCommentCountEntity.from(articleCommentCount)).toDomain()
    }

    override suspend fun findById(articleId: Long): ArticleCommentCount? {
        return articleCommentCountRepository.findById(articleId)?.toDomain()
    }

    override suspend fun increase(articleId: Long): Int {
        return articleCommentCountRepository.increase(articleId)
    }

    override suspend fun decrease(articleId: Long): Int {
        return articleCommentCountRepository.decrease(articleId)
    }
}