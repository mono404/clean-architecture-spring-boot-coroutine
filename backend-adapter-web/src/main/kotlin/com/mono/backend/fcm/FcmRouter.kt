package com.mono.backend.fcm

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class FcmRouter(
    private val fcmHandler: FcmHandler
) {
    @Bean
    fun fcmRoutes(): CoRouterFunctionDsl.() -> Unit = {
        POST("", fcmHandler::registerOrRefresh)
        DELETE("", fcmHandler::delete)
        "send".nest {
            POST("/device", fcmHandler::sendToDevice)
            POST("/multicast", fcmHandler::sendMulticast)
            POST("/topic", fcmHandler::sendToTopic)
        }
    }
}