package com.mono.backend.infra.persistence.post.like.count

import com.mono.backend.domain.post.like.PostLikeCount
import com.mono.backend.port.infra.like.persistence.PostLikeCountPersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostLikeCountPersistenceAdapter(
    private val postLikeCountRepository: PostLikeCountRepository
) : PostLikeCountPersistencePort {
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

    override suspend fun findByIds(postIds: List<Long>): List<PostLikeCount> {
        return postLikeCountRepository.findAllByPostIdIn(postIds).map { it.toDomain() }
    }

    override suspend fun saveWithVersionCheck(postLikeCount: PostLikeCount, previousVersion: Long): Boolean {
        val rowUpdated = postLikeCountRepository.saveWithVersionCheck(postLikeCount, previousVersion)
        return rowUpdated == 1
    }
}