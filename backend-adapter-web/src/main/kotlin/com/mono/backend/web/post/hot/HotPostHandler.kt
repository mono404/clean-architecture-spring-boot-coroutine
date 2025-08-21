package com.mono.backend.web.post.hot

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.hot.HotPostUseCase
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getMemberIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.time.LocalDate

@Component
class HotPostHandler(
    private val hotPostUseCase: HotPostUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberIdOrNull()
        val dateStr = serverRequest.queryParamOrNull("date") ?: LocalDate.now().toString()
        return hotPostUseCase.readAll(memberId, dateStr)?.let { ok(it) } ?: noContent()
    }
}