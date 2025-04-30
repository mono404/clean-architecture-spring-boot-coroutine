package com.mono.backend.view

import com.mono.backend.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class ViewHandler(
    private val articleViewUseCase: ArticleViewUseCase,
): DefaultHandler {
    suspend fun increase(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return articleViewUseCase.increase(articleId, userId)?.let { ok(it) } ?: noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        return ok(articleViewUseCase.count(articleId))
    }
}