package com.mono.backend.handler.post

import com.mono.backend.handler.DefaultHandler
import com.mono.backend.handler.post.PostRequest.Companion.toPost
import com.mono.backend.post.PostUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody

@Component
class PostHandler(
    val postUseCase: PostUseCase
) : DefaultHandler {
    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val postRequest = serverRequest.awaitBody(PostRequest::class)
        return postUseCase.create(postRequest.toPost())
            .let {
                created("posts/${it}", it)
            }
    }

    suspend fun findAll(serverRequest: ServerRequest): ServerResponse {
        return ok(postUseCase.findAll())
    }

    suspend fun find(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("postId").toInt()
        return ok(postUseCase.findById(id))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("postId").toInt()
        val postRequest = serverRequest.awaitBody(PostRequest::class)
        return ok(postUseCase.update(id, postRequest.toPost()))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("postId").toInt()
        return ok(postUseCase.delete(id))
    }
}