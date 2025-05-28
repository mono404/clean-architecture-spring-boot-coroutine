package com.mono.backend.auth

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class AuthRouter(
    private val authHandler: AuthHandler
) {
    @Bean
    fun authRoutes(): CoRouterFunctionDsl.() -> Unit = {
        POST("/social-login", authHandler::login)
        POST("/refresh", authHandler::refresh)
        POST("/logout", authHandler::logout)
    }
}