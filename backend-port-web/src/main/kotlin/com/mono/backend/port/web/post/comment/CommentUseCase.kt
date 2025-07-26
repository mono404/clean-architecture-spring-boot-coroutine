package com.mono.backend.port.web.post.comment

import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequest
import com.mono.backend.port.web.post.comment.dto.CommentPageResponse
import com.mono.backend.port.web.post.comment.dto.CommentResponse
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest

interface CommentUseCase {
    suspend fun create(memberId: Long, postId: Long, request: CommentCreateRequest): CommentResponse
    suspend fun read(commentId: Long): CommentResponse?
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponse
    suspend fun delete(commentId: Long)
    suspend fun readAll(postId: Long, pageRequest: PageRequest): CommentPageResponse
    suspend fun readAllInfiniteScroll(
        postId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        limit: Long
    ): List<CommentResponse>
    suspend fun count(postId: Long): Long
    suspend fun countAll(postIds: List<Long>): Map<Long, Long>
}
