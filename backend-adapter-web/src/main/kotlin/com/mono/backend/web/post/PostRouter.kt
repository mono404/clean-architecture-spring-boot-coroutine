package com.mono.backend.web.post

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class PostRouter(
    private val postHandler: PostHandler
) {
    @Bean
    fun postRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", postHandler::readAllInfiniteScroll)
        GET("/boards/{boardId}/count", postHandler::count)
        GET("/{postId}", postHandler::read)
        PUT("/{postId}", postHandler::update)
        DELETE("/{postId}", postHandler::delete)
        POST("", accept(MediaType.MULTIPART_FORM_DATA), postHandler::create)
        GET("", postHandler::readAll)
    }
}