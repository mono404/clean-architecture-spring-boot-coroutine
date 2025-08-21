package com.mono.backend.infra.persistence.post.like

import com.mono.backend.domain.post.like.PostLike
import com.mono.backend.port.infra.like.persistence.PostLikePersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostLikePersistenceAdapter(
    private val postLikeRepository: PostLikeRepository
) : PostLikePersistencePort {
    override suspend fun save(postLike: PostLike): PostLike {
        return postLikeRepository.save(PostLikeEntity.from(postLike)).toDomain()
    }

    override suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLike? {
        return postLikeRepository.findByPostIdAndMemberId(postId, memberId)?.toDomain()
    }

    override suspend fun findAllByPostIdsAndMemberId(postIds: List<Long>, memberId: Long): List<PostLike> {
        return postLikeRepository.findAllByPostIdInAndMemberId(postIds, memberId).map { it.toDomain() }
    }

    override suspend fun delete(postLike: PostLike) {
        return postLikeRepository.delete(PostLikeEntity.from(postLike))
    }
}