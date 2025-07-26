package com.mono.backend.port.infra.like.persistence

import com.mono.backend.domain.post.like.PostLikeCount

interface PostLikeCountPersistencePort {
    suspend fun save(postLikeCount: PostLikeCount): PostLikeCount
    suspend fun findById(postId: Long): PostLikeCount?
    suspend fun findLockedByPostId(postId: Long): PostLikeCount?
    suspend fun increase(postId: Long): Int
    suspend fun decrease(postId: Long): Int
    suspend fun findByIds(postIds: List<Long>): List<PostLikeCount>
    suspend fun saveWithVersionCheck(postLikeCount: PostLikeCount, previousVersion: Long): Boolean
}