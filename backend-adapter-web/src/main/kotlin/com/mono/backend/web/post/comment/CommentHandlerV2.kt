package com.mono.backend.web.post.comment

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.comment.CommentV2UseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequestV2
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import kotlin.jvm.optionals.getOrNull

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
        val postId = serverRequest.queryParam("postId").get().toLong()
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(
            commentV2UseCase.readAll(
                postId = postId,
                page = page,
                pageSize = pageSize
            )
        )
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.queryParam("postId").get().toLong()
        val lastPath = serverRequest.queryParam("lastPath").getOrNull()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(
            commentV2UseCase.readAllInfiniteScroll(
                postId = postId,
                lastPath = lastPath,
                pageSize = pageSize,
            )
        )
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val requestBody = serverRequest.awaitBody(CommentCreateRequestV2::class)
        return ok(commentV2UseCase.create(requestBody))
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