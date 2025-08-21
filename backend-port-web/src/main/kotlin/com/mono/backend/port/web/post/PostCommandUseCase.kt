package com.mono.backend.port.web.post

import com.mono.backend.port.web.post.dto.PostCreateOrUpdateRequest
import com.mono.backend.port.web.post.dto.PostResponse
import org.springframework.http.codec.multipart.FilePart

interface PostCommandUseCase {
    suspend fun create(memberId: Long, request: PostCreateOrUpdateRequest, mediaFiles: List<FilePart>): PostResponse
    suspend fun update(
        memberId: Long,
        postId: Long,
        request: PostCreateOrUpdateRequest,
        mediaFiles: List<FilePart>
    ): PostResponse

    suspend fun delete(memberId: Long, postId: Long)
}