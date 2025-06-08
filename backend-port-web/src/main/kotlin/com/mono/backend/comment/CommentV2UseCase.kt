package com.mono.backend.comment

import com.mono.backend.comment.request.CommentCreateRequestV2
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponseV2
import com.mono.backend.comment.response.CommentResponseV2

interface CommentV2UseCase {
    suspend fun create(request: CommentCreateRequestV2): CommentResponseV2
    suspend fun read(commentId: Long): CommentResponseV2?
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2
    suspend fun delete(commentId: Long)
    suspend fun readAll(postId: Long, page: Long, pageSize: Long): CommentPageResponseV2
    suspend fun readAllInfiniteScroll(postId: Long, lastPath: String?, pageSize: Long): List<CommentResponseV2>
    suspend fun count(postId: Long): Long
}
