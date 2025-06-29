package com.mono.backend.web.post.hot

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class HotPostRouter(
    private val hotPostHandler: HotPostHandler
) {
    @Bean
    fun hotPostRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/posts/date/{dateStr}", hotPostHandler::readAll)
    }
}