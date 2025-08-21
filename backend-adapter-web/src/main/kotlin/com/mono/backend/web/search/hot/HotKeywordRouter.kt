package com.mono.backend.web.search.hot

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class HotKeywordRouter(
    private val hotKeywordHandler: HotKeywordHandler
) {
    @Bean
    fun hotKeywordRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/hot", hotKeywordHandler::getHotKeyword)
    }
}