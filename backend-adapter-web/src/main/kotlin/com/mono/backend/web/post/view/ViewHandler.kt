package com.mono.backend.web.post.view

import com.mono.backend.port.web.post.view.PostViewUseCase
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class ViewHandler(
    private val postViewUseCase: PostViewUseCase,
) : DefaultHandler {
    suspend fun increase(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        return postViewUseCase.increase(postId, memberId)?.let { ok(it) } ?: noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postViewUseCase.count(postId))
    }
}