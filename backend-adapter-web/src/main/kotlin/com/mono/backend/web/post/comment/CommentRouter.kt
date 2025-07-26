package com.mono.backend.web.post.comment

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class CommentRouter(
    private val commentHandler: CommentHandlerV1
) {
    @Bean
    fun postCommentRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", commentHandler::readAllInfiniteScroll)
        GET("/{commentId}", commentHandler::read)
        DELETE("/{commentId}", commentHandler::delete)
        PUT("/{commentId}", commentHandler::update)
        POST("", commentHandler::create)
        GET("", commentHandler::readAll)
    }
}