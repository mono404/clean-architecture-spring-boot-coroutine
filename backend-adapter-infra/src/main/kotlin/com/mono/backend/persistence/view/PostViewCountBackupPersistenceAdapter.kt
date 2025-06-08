package com.mono.backend.persistence.view

import com.mono.backend.view.PostViewCount
import org.springframework.stereotype.Repository

@Repository
class PostViewCountBackupPersistenceAdapter(
    private val postViewCountBackUpRepository: PostViewCountBackUpRepository
) {
    suspend fun save(postViewCount: PostViewCount): PostViewCount {
        return postViewCountBackUpRepository.save(PostViewCountEntity.from(postViewCount)).toDomain()
    }

    suspend fun findById(postId: Long): PostViewCount? {
        return postViewCountBackUpRepository.findById(postId)?.toDomain()
    }

    suspend fun updateViewCount(postId: Long, viewCount: Long): Int {
        return postViewCountBackUpRepository.updateViewCount(postId, viewCount)
    }
}