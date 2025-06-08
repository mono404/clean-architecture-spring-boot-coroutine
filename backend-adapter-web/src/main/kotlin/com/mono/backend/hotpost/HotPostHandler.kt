package com.mono.backend.hotpost

import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
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