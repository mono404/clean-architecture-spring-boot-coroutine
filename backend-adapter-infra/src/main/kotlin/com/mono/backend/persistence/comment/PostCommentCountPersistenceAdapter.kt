package com.mono.backend.persistence.comment

import com.mono.backend.comment.PostCommentCount
import org.springframework.stereotype.Repository

@Repository
class PostCommentCountPersistenceAdapter(
    private val postCommentCountRepository: PostCommentCountRepository
):PostCommentCountPersistencePort {
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
}