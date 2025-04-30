package com.mono.backend.like

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class LikeRouter(
    private val likeHandler: LikeHandler
) {
    @Bean
    fun likeRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/user/{userId}", likeHandler::read)
        GET("/count", likeHandler::count)
        POST("/user/{userId}/pessimistic-lock-1", likeHandler::likePessimisticLock1)
        DELETE("/user/{userId}/pessimistic-lock-1", likeHandler::unlikePessimisticLock1)
        POST("/user/{userId}/pessimistic-lock-2", likeHandler::likePessimisticLock2)
        DELETE("/user/{userId}/pessimistic-lock-2", likeHandler::unlikePessimisticLock2)
        POST("/user/{userId}/optimistic-lock-1", likeHandler::likeOptimisticLock)
        DELETE("/user/{userId}/optimistic-lock-1", likeHandler::unlikeOptimisticLock)
    }
}