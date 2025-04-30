package com.mono.backend.hotarticle

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class HotArticleRouter(
    private val hotArticleHandler: HotArticleHandler
) {
    @Bean
    fun hotArticleRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/articles/date/{dateStr}", hotArticleHandler::readAll)
    }
}