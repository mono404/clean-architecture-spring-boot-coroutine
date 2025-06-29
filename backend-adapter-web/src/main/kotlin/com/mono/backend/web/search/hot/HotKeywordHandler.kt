package com.mono.backend.web.search.hot

import com.mono.backend.domain.search.hot.HotKeywordScope
import com.mono.backend.port.web.search.hot.HotKeywordUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class HotKeywordHandler(
    private val hotKeywordUseCase: HotKeywordUseCase
) : DefaultHandler {
    suspend fun getHotKeyword(serverRequest: ServerRequest): ServerResponse {
        val type = serverRequest.queryParamOrNull("type") ?: "post"
        val scope = HotKeywordScope.valueOf(serverRequest.queryParamOrNull("scope")?.uppercase() ?: "DAILY")

        return ok(hotKeywordUseCase.getHotKeywords(type, scope))
    }
}