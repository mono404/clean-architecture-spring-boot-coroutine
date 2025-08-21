package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.post.comment.PostCommentCount
import com.mono.backend.port.infra.comment.persistence.PostCommentCountPersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostCommentCountPersistenceAdapter(
    private val postCommentCountRepository: PostCommentCountRepository
) : PostCommentCountPersistencePort {
    override suspend fun save(postCommentCount: PostCommentCount): PostCommentCount {
        return postCommentCountRepository.save(PostCommentCountEntity.from(postCommentCount)).toDomain()
    }

    override suspend fun findById(postId: Long): PostCommentCount? {
        return postCommentCountRepository.findById(postId)?.toDomain()
    }

    override suspend fun increase(postId: Long): Int {
        return postCommentCountRepository.increase(postId)
    }

    override suspend fun decrease(postId: Long): Int {
        return postCommentCountRepository.decrease(postId)
    }

    override suspend fun findByIds(postIds: List<Long>): List<PostCommentCount> {
        return postCommentCountRepository.findAllByPostIdIn(postIds).map { it.toDomain() }
    }
}