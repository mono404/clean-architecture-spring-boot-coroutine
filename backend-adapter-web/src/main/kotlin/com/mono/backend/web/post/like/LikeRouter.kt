package com.mono.backend.web.post.like

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class LikeRouter(
    private val likeHandler: LikeHandler
) {
    @Bean
    fun likeRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/member/{memberId}", likeHandler::read)
        GET("/count", likeHandler::count)
        POST("/member/{memberId}/pessimistic-lock-1", likeHandler::likePessimisticLock1)
        DELETE("/member/{memberId}/pessimistic-lock-1", likeHandler::unlikePessimisticLock1)
        POST("/member/{memberId}/pessimistic-lock-2", likeHandler::likePessimisticLock2)
        DELETE("/member/{memberId}/pessimistic-lock-2", likeHandler::unlikePessimisticLock2)
        POST("/member/{memberId}/optimistic-lock-1", likeHandler::likeOptimisticLock)
        DELETE("/member/{memberId}/optimistic-lock-1", likeHandler::unlikeOptimisticLock)
    }
}