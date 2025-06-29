package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePort
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

    override suspend fun countBy(postId: Long, parentCommentId: Long, limit: Long): Long {
        return commentRepository.countBy(postId, parentCommentId, limit)
    }

    override suspend fun delete(comment: Comment) {
        return commentRepository.delete(CommentEntity.from(comment))
    }

    override suspend fun findAll(postId: Long, offset: Long, limit: Long): List<Comment> {
        return commentRepository.findAll(postId, offset, limit).map(CommentEntity::toDomain)
    }

    override suspend fun count(postId: Long, limit: Long): Long {
        return commentRepository.count(postId, limit)
    }

    override suspend fun findAllInfiniteScroll(postId: Long, limit: Long): List<Comment> {
        return commentRepository.findAllInfiniteScroll(postId, limit).map(CommentEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(
        postId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long
    ): List<Comment> {
        return commentRepository.findAllInfiniteScroll(postId, lastCommentId, lastParentCommentId, limit)
            .map(CommentEntity::toDomain)
    }
}