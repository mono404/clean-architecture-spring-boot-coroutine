package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.post.comment.CommentV2
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePortV2
import org.springframework.stereotype.Repository

@Repository
class CommentPersistenceAdapterV2(
    private val commentRepositoryV2: CommentRepositoryV2
) : CommentPersistencePortV2 {
    override suspend fun save(commentV2: CommentV2): CommentV2 {
        return commentRepositoryV2.save(
            CommentV2Entity.from(
                commentV2
            )
        ).toDomain()
    }

    override suspend fun findById(commentId: Long): CommentV2? {
        return commentRepositoryV2.findById(commentId)?.toDomain()
    }

    override suspend fun findByPath(path: String): CommentV2? {
        return commentRepositoryV2.findByPath(path)?.toDomain()
    }

    override suspend fun findDescendantsTopPath(postId: Long, pathPrefix: String): String? {
        return commentRepositoryV2.findDescendantsTopPath(postId, pathPrefix)
    }

    override suspend fun delete(commentV2: CommentV2) {
        return commentRepositoryV2.delete(
            CommentV2Entity.from(
                commentV2
            )
        )
    }

    override suspend fun findAll(postId: Long, offset: Long, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAll(postId, offset, limit).map(CommentV2Entity::toDomain)
    }

    override suspend fun count(postId: Long, limit: Long): Long {
        return commentRepositoryV2.count(postId, limit)
    }

    override suspend fun findAllInfiniteScroll(postId: Long, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAllInfiniteScroll(postId, limit).map(CommentV2Entity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(postId: Long, lastPath: String, limit: Long): List<CommentV2> {
        return commentRepositoryV2.findAllInfiniteScroll(postId, lastPath, limit).map(CommentV2Entity::toDomain)
    }

    override suspend fun findAllByPostId(postId: Long): List<CommentV2> {
        return commentRepositoryV2.findAllByPostId(postId).map { it.toDomain() }
    }
}