package com.mono.backend.router.post

import com.mono.backend.handler.post.PostHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class PostRouter(
    private val postHandler: PostHandler
) {
    @Bean
    fun routes(): CoRouterFunctionDsl.() -> Unit = {
        POST("", postHandler::create)
        GET("", postHandler::findAll)
        "/{postId}".nest {
            GET("", postHandler::find)
            PUT("", postHandler::update)
            DELETE("", postHandler::delete)
        }
    }
}

