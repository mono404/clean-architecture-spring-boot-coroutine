package com.mono.backend.view

interface PostViewUseCase {
    suspend fun increase(postId: Long, memberId: Long): Long?
    suspend fun count(postId: Long): Long
}