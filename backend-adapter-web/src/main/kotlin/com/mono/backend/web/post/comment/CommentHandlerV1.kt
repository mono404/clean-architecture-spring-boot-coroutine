package com.mono.backend.web.post.comment

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.comment.CommentUseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequest
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import com.mono.backend.web.common.RequestAttributeUtils.getPageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import kotlin.jvm.optionals.getOrNull

@Component
class CommentHandlerV1(
    private val commentUseCase: CommentUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return commentUseCase.read(commentId)?.let { ok(it) } ?: noContent()
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val pageRequest = serverRequest.getPageRequest()
        return ok(commentUseCase.readAll(postId, pageRequest))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastParentCommentId = serverRequest.queryParam("lastParentCommentId").getOrNull()?.toLong()
        val lastCommentId = serverRequest.queryParam("lastCommentId").getOrNull()?.toLong()
        return ok(
            commentUseCase.readAllInfiniteScroll(
                postId = postId,
                lastParentCommentId = lastParentCommentId,
                lastCommentId = lastCommentId,
                limit = pageSize
            )
        )
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        val requestBody = serverRequest.awaitBody(CommentCreateRequest::class)
        return ok(commentUseCase.create(memberId, postId, requestBody))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        val requestBody = serverRequest.awaitBody(CommentUpdateRequest::class)
        return ok(commentUseCase.update(commentId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return ok(commentUseCase.delete(commentId))
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return ok(commentUseCase.delete(commentId))
    }
}