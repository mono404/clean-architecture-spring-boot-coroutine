package com.mono.backend.article

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class ArticleRouter(
    private val articleHandler: ArticleHandler
) {
    @Bean
    fun articleRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", articleHandler::readAllInfiniteScroll)
        GET("/boards/{boardId}/count", articleHandler::count)
        GET("/{articleId}", articleHandler::read)
        PUT("/{articleId}", articleHandler::update)
        DELETE("/{articleId}", articleHandler::delete)
        POST("", articleHandler::create)
        GET("", articleHandler::readAll)
    }
}