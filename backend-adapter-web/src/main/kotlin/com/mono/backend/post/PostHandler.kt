package com.mono.backend.post

import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import com.mono.backend.post.request.PostCreateRequest
import com.mono.backend.post.request.PostUpdateRequest
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
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(postQueryUseCase.readAll(boardId, page, pageSize))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastPostId = serverRequest.queryParam("lastPostId").getOrNull()?.toLong()
        return ok(postQueryUseCase.readAllInfiniteScroll(boardId, lastPostId, pageSize))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val parts = serverRequest.awaitMultipartData()
        val request = PostCreateRequest.fromPart(parts, memberId)
        val files = parts["mediaFiles"]?.filterIsInstance<FilePart>()

//        val postRequest = (parts["postCreatedRequest"]?.firstOrNull() as? FormFieldPart)?.value()
//        println("postRequest = $postRequest")
//        val postDto = objectMapper.readValue(postRequest, PostCreateRequest::class.java)
//        println("postDto = $postDto")

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
        return ok(postQueryUseCase.count(boardId))
    }
}