package com.mono.backend.infra.persistence.post.like

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : CoroutineCrudRepository<PostLikeEntity, Long> {
    suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLikeEntity?
    suspend fun findAllByPostIdInAndMemberId(postId: List<Long>, memberId: Long): List<PostLikeEntity>
}