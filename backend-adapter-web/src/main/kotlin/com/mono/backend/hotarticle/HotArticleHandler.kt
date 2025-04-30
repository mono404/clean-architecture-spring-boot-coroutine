package com.mono.backend.hotarticle

import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class HotArticleHandler(
    private val hotArticleUseCase: HotArticleUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val dateStr = serverRequest.pathVariable("dateStr")
        return hotArticleUseCase.readAll(dateStr)?.let { ok(it) } ?: noContent()
    }
}