package com.mono.backend.web.auth

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class AuthRouter(
    private val authHandler: AuthHandler
) {
    @Bean
    fun authRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/test", authHandler::test)
        POST("/social-login", authHandler::login)
        POST("/refresh", authHandler::refresh)
        POST("/logout", authHandler::logout)
    }
}