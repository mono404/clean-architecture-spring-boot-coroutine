package com.mono.backend.persistence.comment

import com.mono.backend.comment.CommentV2
import org.springframework.stereotype.Repository

@Repository
class CommentPersistenceAdapterV2(
    private val commentRepositoryV2: CommentRepositoryV2
): CommentPersistencePortV2 {
    override suspend fun save(commentV2: CommentV2): CommentV2 {
        return commentRepositoryV2.save(CommentV2Entity.from(commentV2)).toDomain()
    }

    override suspend fun findById(commentId: Long): CommentV2? {
        return commentRepositoryV2.findById(commentId)?.toDomain()
    }

    override suspend fun findByPath(path: String): CommentV2? {
        return commentRepositoryV2.findByPath(path)?.toDomain()
    }

    override suspend fun findDescendantsTopPath(articleId: Long, pathPrefix: String): String? {
        return commentRepositoryV2.findDescendantsTopPath(articleId, pathPrefix)
    }

    override suspend fun delete(commentV2: CommentV2) {
        return commentRepositoryV2.delete(CommentV2Entity.from(commentV2))
    }

    override suspend fun findAll(articleId: Long, offset: Long, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAll(articleId, offset, limit).map(CommentV2Entity::toDomain)
    }

    override suspend fun count(articleId: Long, limit: Long): Long {
        return commentRepositoryV2.count(articleId, limit)
    }

    override suspend fun findAllInfiniteScroll(articleId: Long, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAllInfiniteScroll(articleId, limit).map(CommentV2Entity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(articleId: Long, lastPath: String, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAllInfiniteScroll(articleId, lastPath, limit).map(CommentV2Entity::toDomain)
    }
}