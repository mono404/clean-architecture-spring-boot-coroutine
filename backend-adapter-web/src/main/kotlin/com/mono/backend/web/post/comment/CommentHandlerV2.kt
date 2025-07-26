package com.mono.backend.web.post.comment

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.comment.CommentV2UseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequestV2
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getCursorRequest
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import com.mono.backend.web.common.RequestAttributeUtils.getPageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody

@Component
class CommentHandlerV2(
    private val commentV2UseCase: CommentV2UseCase
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return commentV2UseCase.read(commentId)?.let { ok(it) } ?: noContent()
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val pageRequest = serverRequest.getPageRequest()
        return ok(commentV2UseCase.readAll(postId, pageRequest))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val cursorRequest = serverRequest.getCursorRequest()
        return ok(commentV2UseCase.readAllInfiniteScroll(postId, cursorRequest))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        val requestBody = serverRequest.awaitBody(CommentCreateRequestV2::class)
        return ok(commentV2UseCase.create(memberId, postId, requestBody))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        return ok("")
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return ok(commentV2UseCase.delete(commentId))
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(commentV2UseCase.count(postId))
    }
}