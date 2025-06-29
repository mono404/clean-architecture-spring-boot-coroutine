package com.mono.backend.port.web.post

import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.web.post.dto.PostCreateRequest
import com.mono.backend.port.web.post.dto.PostResponse
import com.mono.backend.port.web.post.dto.PostUpdateRequest
import org.springframework.http.codec.multipart.FilePart

interface PostCommandUseCase {
    suspend fun create(request: PostCreateRequest, mediaFiles: List<FilePart>?): PostResponse
    suspend fun update(postId: Long, request: PostUpdateRequest): PostResponse
    suspend fun delete(postId: Long)
    suspend fun count(boardType: BoardType): Long
}