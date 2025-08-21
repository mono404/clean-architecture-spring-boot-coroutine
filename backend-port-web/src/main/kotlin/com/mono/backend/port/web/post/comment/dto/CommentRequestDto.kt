package com.mono.backend.port.web.post.comment.dto

data class CommentCreateRequest(
    val content: String,
    val parentCommentId: String?
)

data class CommentCreateRequestV2(
    val content: String,
    val parentPath: String?,
)

data class CommentUpdateRequest(
    val content: String,
)