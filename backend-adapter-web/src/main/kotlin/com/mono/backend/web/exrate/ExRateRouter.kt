package com.mono.backend.web.exrate

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class ExRateRouter(
    private val exRateHandler: ExRateHandler
) {
    @Bean
    fun exRateRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("", exRateHandler::getExRate)
    }
}