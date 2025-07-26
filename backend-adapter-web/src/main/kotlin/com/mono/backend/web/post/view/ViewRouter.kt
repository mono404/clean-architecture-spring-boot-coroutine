package com.mono.backend.web.post.view

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class ViewRouter(
    private val viewHandler: ViewHandler
) {
    @Bean
    fun postViewRoutes(): CoRouterFunctionDsl.() -> Unit = {
        POST("", viewHandler::increase)
        GET("/count", viewHandler::count)
    }
}