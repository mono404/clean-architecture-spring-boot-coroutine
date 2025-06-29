package com.mono.backend.port.infra.comment.persistence

import com.mono.backend.domain.post.comment.PostCommentCount

interface PostCommentCountPersistencePort {
    suspend fun save(postCommentCount: PostCommentCount): PostCommentCount
    suspend fun findById(postId: Long): PostCommentCount?
    suspend fun increase(postId: Long): Int
    suspend fun decrease(postId: Long): Int
}