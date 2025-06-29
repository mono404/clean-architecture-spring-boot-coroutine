package com.mono.backend.port.infra.like.persistence

import com.mono.backend.domain.post.like.PostLike

interface PostLikePersistencePort {
    suspend fun save(postLike: PostLike): PostLike
    suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLike?
    suspend fun delete(postLike: PostLike)
}