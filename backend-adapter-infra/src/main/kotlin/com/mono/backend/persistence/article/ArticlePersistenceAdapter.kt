package com.mono.backend.persistence.article

import com.mono.backend.article.Article
import org.springframework.stereotype.Repository

@Repository
class ArticlePersistenceAdapter(
    private val articleRepository: ArticleRepository,
): ArticlePersistencePort {
    override suspend fun save(article: Article): Article {
        return articleRepository.save(ArticleEntity.from(article)).toDomain()
    }

    override suspend fun findById(articleId: Long): Article? {
        return articleRepository.findById(articleId)?.toDomain()
    }

    override suspend fun findAll(boardId: Long, offset: Long, limit: Long): List<Article> {
        return articleRepository.findAll(boardId, offset, limit).map(ArticleEntity::toDomain)
    }

    override suspend fun count(boardId: Long, limit: Long): Long {
        return articleRepository.count(boardId, limit)
    }

    override suspend fun findAllInfiniteScroll(boardId: Long, limit: Long): List<Article> {
        return articleRepository.findAllInfiniteScroll(boardId, limit).map(ArticleEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(boardId: Long, limit: Long, lastArticleId: Long): List<Article> {
        return articleRepository.findAllInfiniteScroll(boardId, limit, lastArticleId).map(ArticleEntity::toDomain)
    }

    override suspend fun delete(article: Article) {
        return articleRepository.delete(ArticleEntity.from(article))
    }
}