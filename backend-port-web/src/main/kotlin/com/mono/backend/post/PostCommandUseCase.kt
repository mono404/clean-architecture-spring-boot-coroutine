package com.mono.backend.post

import com.mono.backend.post.request.PostCreateRequest
import com.mono.backend.post.request.PostUpdateRequest
import com.mono.backend.post.response.PostResponse
import org.springframework.http.codec.multipart.FilePart

interface PostCommandUseCase {
    suspend fun create(request: PostCreateRequest, mediaFiles: List<FilePart>?): PostResponse
    suspend fun update(postId: Long, request: PostUpdateRequest): PostResponse
    suspend fun delete(postId: Long)
    suspend fun count(boardId: Long): Long
}