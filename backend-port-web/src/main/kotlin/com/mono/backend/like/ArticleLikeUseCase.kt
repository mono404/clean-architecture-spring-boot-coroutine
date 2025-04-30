package com.mono.backend.like

import com.mono.backend.like.response.ArticleLikeResponse

interface ArticleLikeUseCase {
    suspend fun read(articleId: Long, userId: Long): ArticleLikeResponse?
    suspend fun likePessimisticLock1(articleId: Long, userId: Long)
    suspend fun unlikePessimisticLock1(articleId: Long, userId: Long)
    suspend fun likePessimisticLock2(articleId: Long, userId: Long): ArticleLikeCount
    suspend fun unlikePessimisticLock2(articleId: Long, userId: Long): ArticleLikeCount?
    suspend fun likeOptimisticLock(articleId: Long, userId: Long): ArticleLikeCount
    suspend fun unlikeOptimisticLock(articleId: Long, userId: Long): ArticleLikeCount?
    suspend fun count(articleId: Long): Long
}