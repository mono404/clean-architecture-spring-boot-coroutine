package com.mono.backend.comment

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class CommentRouter(
    private val commentHandler: CommentHandler
) {
    @Bean
    fun commentRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", commentHandler::readAllInfiniteScroll)
        GET("/{commentId}", commentHandler::read)
        DELETE("/{commentId}", commentHandler::delete)
        PUT("/{commentId}", commentHandler::update)
        GET("", commentHandler::readAll)
        POST("", commentHandler::create)
    }
}