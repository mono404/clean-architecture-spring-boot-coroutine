package com.mono.backend.post

import com.mono.backend.post.response.PostReadPageResponse
import com.mono.backend.post.response.PostReadResponse

interface PostQueryUseCase {
    suspend fun read(postId: Long): PostReadResponse
    suspend fun readAll(boardId: Long, page: Long, pageSize: Long): PostReadPageResponse
    suspend fun readAllInfiniteScroll(boardId: Long, lastPostId: Long?, pageSize: Long): List<PostReadResponse>
    suspend fun count(boardId: Long): Long
}