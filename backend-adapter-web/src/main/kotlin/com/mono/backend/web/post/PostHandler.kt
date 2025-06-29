package com.mono.backend.web.post

import com.mono.backend.common.log.logger
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.web.post.PostCommandUseCase
import com.mono.backend.port.web.post.PostQueryUseCase
import com.mono.backend.port.web.post.dto.PostCreateRequest
import com.mono.backend.port.web.post.dto.PostUpdateRequest
import com.mono.backend.web.common.DefaultHandler
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitMultipartData
import kotlin.jvm.optionals.getOrNull

@Component
class PostHandler(
    private val postCommandUseCase: PostCommandUseCase,
    private val postQueryUseCase: PostQueryUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postQueryUseCase.read(postId))
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val boardType = BoardType.fromId(boardId)
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val pageRequest = PageRequest(page, pageSize)
        return ok(postQueryUseCase.readAll(boardType, pageRequest))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val boardType = BoardType.fromId(boardId)
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastPostId = serverRequest.queryParam("lastPostId").getOrNull()?.toLong()
        return ok(postQueryUseCase.readAllInfiniteScroll(boardType, lastPostId, pageSize))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val parts = serverRequest.awaitMultipartData()
        val request = PostCreateRequest.fromPart(parts, memberId)
        val files = parts["mediaFiles"]?.filterIsInstance<FilePart>() ?: emptyList()

        return ok(postCommandUseCase.create(request, files))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val requestBody = serverRequest.awaitBody(PostUpdateRequest::class)
        return ok(postCommandUseCase.update(postId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        postCommandUseCase.delete(postId)
        return noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.pathVariable("boardId").toLong()
        val boardType = BoardType.fromId(boardId)
        return ok(postQueryUseCase.count(boardType))
    }
}