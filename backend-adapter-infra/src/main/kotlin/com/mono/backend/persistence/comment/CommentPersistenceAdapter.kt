package com.mono.backend.persistence.comment

import com.mono.backend.comment.Comment
import org.springframework.stereotype.Repository

@Repository
class CommentPersistenceAdapter(
    private val commentRepository: CommentRepository
) : CommentPersistencePort {
    override suspend fun save(comment: Comment): Comment {
        return commentRepository.save(CommentEntity.from(comment)).toDomain()
    }

    override suspend fun findById(commentId: Long): Comment? {
        return commentRepository.findById(commentId)?.toDomain()
    }

    override suspend fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long {
        return commentRepository.countBy(articleId, parentCommentId, limit)
    }

    override suspend fun delete(comment: Comment) {
        return commentRepository.delete(CommentEntity.from(comment))
    }

    override suspend fun findAll(articleId: Long, offset: Long, limit: Long): List<Comment> {
        return commentRepository.findAll(articleId, offset, limit).map(CommentEntity::toDomain)
    }

    override suspend fun count(articleId: Long, limit: Long): Long {
        return commentRepository.count(articleId, limit)
    }

    override suspend fun findAllInfiniteScroll(articleId: Long, limit: Long): List<Comment> {
        return commentRepository.findAllInfiniteScroll(articleId, limit).map(CommentEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(
        articleId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long
    ): List<Comment> {
        return commentRepository.findAllInfiniteScroll(articleId, lastCommentId, lastParentCommentId, limit)
            .map(CommentEntity::toDomain)
    }
}