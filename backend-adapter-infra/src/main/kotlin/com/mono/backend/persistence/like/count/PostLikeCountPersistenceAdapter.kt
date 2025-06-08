package com.mono.backend.persistence.like.count

import com.mono.backend.like.PostLikeCount
import com.mono.backend.persistence.like.PostLikeCountPersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostLikeCountPersistenceAdapter(
    private val postLikeCountRepository: PostLikeCountRepository
): PostLikeCountPersistencePort {
    override suspend fun save(postLikeCount: PostLikeCount): PostLikeCount {
        return postLikeCountRepository.save(PostLikeCountEntity.from(postLikeCount)).toDomain()
    }

    override suspend fun findById(postId: Long): PostLikeCount? {
        return postLikeCountRepository.findById(postId)?.toDomain()
    }

    override suspend fun findLockedByPostId(postId: Long): PostLikeCount? {
        return postLikeCountRepository.findLockedByPostId(postId)?.toDomain()
    }

    override suspend fun increase(postId: Long): Int {
        return postLikeCountRepository.increase(postId)
    }

    override suspend fun decrease(postId: Long): Int {
        return postLikeCountRepository.decrease(postId)
    }
}