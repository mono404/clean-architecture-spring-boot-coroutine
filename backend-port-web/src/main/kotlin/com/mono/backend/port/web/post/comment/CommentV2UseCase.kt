package com.mono.backend.port.web.post.comment

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequestV2
import com.mono.backend.port.web.post.comment.dto.CommentPageResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest

interface CommentV2UseCase {
    suspend fun create(memberId: Long, postId: Long, request: CommentCreateRequestV2): CommentResponseV2
    suspend fun read(commentId: Long): CommentResponseV2?
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2
    suspend fun delete(commentId: Long)
    suspend fun readAll(postId: Long, pageRequest: PageRequest): CommentPageResponseV2
    suspend fun readAllInfiniteScroll(postId: Long, cursorRequest: CursorRequest): List<CommentResponseV2>
    suspend fun count(postId: Long): Long
    suspend fun countAll(postIds: List<Long>): Map<Long, Long>
}
