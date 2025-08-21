package com.mono.backend.web.search

import com.mono.backend.web.search.hot.HotKeywordRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class SearchRouter(
    private val searchHandler: SearchHandler,
    private val hotKeywordRouter: HotKeywordRouter
) {
    @Bean
    fun searchRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("", searchHandler::search)
        "hot".nest(hotKeywordRouter.hotKeywordRoutes())
    }
}