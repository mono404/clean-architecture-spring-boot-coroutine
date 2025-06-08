package com.mono.backend.persistence.like

import com.mono.backend.like.PostLikeCount

interface PostLikeCountPersistencePort {
    suspend fun save(postLikeCount: PostLikeCount): PostLikeCount
    suspend fun findById(postId: Long): PostLikeCount?
    suspend fun findLockedByPostId(postId: Long): PostLikeCount?
    suspend fun increase(postId: Long): Int
    suspend fun decrease(postId: Long): Int
}