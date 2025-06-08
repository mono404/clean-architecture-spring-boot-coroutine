package com.mono.backend.like

import com.mono.backend.like.response.PostLikeResponse

interface PostLikeUseCase {
    suspend fun read(postId: Long, memberId: Long): PostLikeResponse?
    suspend fun likePessimisticLock1(postId: Long, memberId: Long)
    suspend fun unlikePessimisticLock1(postId: Long, memberId: Long)
    suspend fun likePessimisticLock2(postId: Long, memberId: Long): PostLikeCount
    suspend fun unlikePessimisticLock2(postId: Long, memberId: Long): PostLikeCount?
    suspend fun likeOptimisticLock(postId: Long, memberId: Long): PostLikeCount
    suspend fun unlikeOptimisticLock(postId: Long, memberId: Long): PostLikeCount?
    suspend fun count(postId: Long): Long
}