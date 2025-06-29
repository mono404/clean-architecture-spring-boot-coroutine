package com.mono.backend.web.post.hot

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.hot.HotPostUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class HotPostHandler(
    private val hotPostUseCase: HotPostUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val dateStr = serverRequest.pathVariable("dateStr")
        return hotPostUseCase.readAll(dateStr)?.let { ok(it) } ?: noContent()
    }
}