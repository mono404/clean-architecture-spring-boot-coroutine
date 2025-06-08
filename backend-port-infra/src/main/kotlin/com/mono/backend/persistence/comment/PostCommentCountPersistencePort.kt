package com.mono.backend.persistence.comment

import com.mono.backend.comment.PostCommentCount

interface PostCommentCountPersistencePort {
    suspend fun save(postCommentCount: PostCommentCount): PostCommentCount
    suspend fun findById(postId: Long): PostCommentCount?
    suspend fun increase(postId: Long): Int
    suspend fun decrease(postId: Long): Int
}