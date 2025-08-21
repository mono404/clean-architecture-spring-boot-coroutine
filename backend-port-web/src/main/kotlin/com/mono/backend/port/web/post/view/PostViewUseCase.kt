package com.mono.backend.port.web.post.view

interface PostViewUseCase {
    suspend fun increase(postId: Long, memberId: Long): Long?
    suspend fun count(postId: Long): Long
}