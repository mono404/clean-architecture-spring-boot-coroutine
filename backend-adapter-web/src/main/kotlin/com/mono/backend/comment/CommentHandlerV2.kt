package com.mono.backend.comment

import com.mono.backend.comment.request.CommentCreateRequestV2
import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
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
        val articleId = serverRequest.queryParam("articleId").get().toLong()
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(
            commentV2UseCase.readAll(
                articleId = articleId,
                page = page,
                pageSize = pageSize
            )
        )
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.queryParam("articleId").get().toLong()
        val lastPath = serverRequest.queryParam("lastPath").getOrNull()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(
            commentV2UseCase.readAllInfiniteScroll(
                articleId = articleId,
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
        val commentId = serverRequest.pathVariable("articleId").toLong()
        return ok(commentV2UseCase.count(commentId))
    }
}