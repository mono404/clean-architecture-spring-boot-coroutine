package com.mono.backend.comment

import com.mono.backend.comment.request.CommentCreateRequest
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponse
import com.mono.backend.comment.response.CommentResponse

interface CommentUseCase {
    suspend fun create(request: CommentCreateRequest): CommentResponse
    suspend fun read(commentId: Long): CommentResponse?
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponse
    suspend fun delete(commentId: Long)
    suspend fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse
    suspend fun readAllInfiniteScroll(
        articleId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        limit: Long
    ): List<CommentResponse>
}
