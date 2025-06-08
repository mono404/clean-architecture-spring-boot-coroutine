package com.mono.backend.persistence.like

import com.mono.backend.like.PostLike

interface PostLikePersistencePort {
    suspend fun save(postLike: PostLike): PostLike
    suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLike?
    suspend fun delete(postLike: PostLike)
}