package com.mono.backend.web.exrate

import com.mono.backend.port.web.exrate.ExRateUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class ExRateHandler(
    private val exRateUseCase: ExRateUseCase
) : DefaultHandler {
    suspend fun getExRate(serverRequest: ServerRequest): ServerResponse {
        return serverRequest.queryParamOrNull("currency")?.let {
            ok(exRateUseCase.getExRate(it))
        } ?: badRequest()
    }
}