package com.mono.backend.persistence.like

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : CoroutineCrudRepository<PostLikeEntity, Long> {
    suspend fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLikeEntity?
}