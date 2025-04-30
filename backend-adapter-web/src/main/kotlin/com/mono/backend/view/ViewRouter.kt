package com.mono.backend.view

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class ViewRouter(
    private val viewHandler: ViewHandler
) {
    @Bean
    fun viewRoutes(): CoRouterFunctionDsl.() -> Unit = {
        POST("/users/{userId}", viewHandler::increase)
        GET("/count", viewHandler::count)
    }
}