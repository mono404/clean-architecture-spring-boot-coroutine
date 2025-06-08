package com.mono.backend.persistence.like

import com.mono.backend.like.PostLike
import org.springframework.stereotype.Repository

@Repository
class PostLikePersistenceAdapter(
    private val postLikeRepository: PostLikeRepository
) : PostLikePersistencePort{
    override suspend fun save(postLike: PostLike): PostLike {
        return postLikeRepository.save(PostLikeEntity.from(postLike)).toDomain()
    }

    override suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLike? {
        return postLikeRepository.findByPostIdAndMemberId(postId, memberId)?.toDomain()
    }

    override suspend fun delete(postLike: PostLike) {
        return postLikeRepository.delete(PostLikeEntity.from(postLike))
    }
}