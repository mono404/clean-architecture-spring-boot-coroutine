package com.mono.backend.port.web.post.comment

import com.mono.backend.port.web.post.comment.dto.CommentCreateRequestV2
import com.mono.backend.port.web.post.comment.dto.CommentPageResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest

interface CommentV2UseCase {
    suspend fun create(request: CommentCreateRequestV2): CommentResponseV2
    suspend fun read(commentId: Long): CommentResponseV2?
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2
    suspend fun delete(commentId: Long)
    suspend fun readAll(postId: Long, page: Long, pageSize: Long): CommentPageResponseV2
    suspend fun readAllInfiniteScroll(postId: Long, lastPath: String?, pageSize: Long): List<CommentResponseV2>
    suspend fun count(postId: Long): Long
}
