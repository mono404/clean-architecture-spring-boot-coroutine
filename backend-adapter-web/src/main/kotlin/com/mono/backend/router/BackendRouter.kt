package com.mono.backend.router

import com.mono.backend.router.post.PostRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class BackendRouter(
    private val postRouter: PostRouter,
) {
    @Bean
    fun onBoardingRoutes(): RouterFunction<ServerResponse> = coRouter {
        "/posts".nest(postRouter.routes())
    }
}