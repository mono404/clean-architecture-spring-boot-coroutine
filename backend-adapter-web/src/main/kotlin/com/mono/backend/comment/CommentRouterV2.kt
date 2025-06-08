package com.mono.backend.comment

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class CommentRouterV2(
    private val commentHandlerV2: CommentHandlerV2
) {
    @Bean
    fun commentRoutesV2(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", commentHandlerV2::readAllInfiniteScroll)
        GET("/posts/{postId}/count", commentHandlerV2::count)
        GET("/{commentId}", commentHandlerV2::read)
        DELETE("/{commentId}", commentHandlerV2::delete)
        PUT("/{commentId}", commentHandlerV2::update)
        GET("", commentHandlerV2::readAll)
        POST("", commentHandlerV2::create)
    }
}