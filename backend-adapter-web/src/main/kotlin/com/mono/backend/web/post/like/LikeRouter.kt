package com.mono.backend.web.post.like

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class LikeRouter(
    private val likeHandler: LikeHandler
) {
    @Bean
    fun postLikeRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("", likeHandler::read)
        POST("/pessimistic-lock-1", likeHandler::likePessimisticLock1)
        DELETE("/pessimistic-lock-1", likeHandler::unlikePessimisticLock1)
        POST("/pessimistic-lock-2", likeHandler::likePessimisticLock2)
        DELETE("/pessimistic-lock-2", likeHandler::unlikePessimisticLock2)
        POST("/optimistic-lock-1", likeHandler::likeOptimisticLock)
        DELETE("/optimistic-lock-1", likeHandler::unlikeOptimisticLock)
        GET("/count", likeHandler::count)
    }
}