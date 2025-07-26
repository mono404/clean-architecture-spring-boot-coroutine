package com.mono.backend.web.notification

import com.mono.backend.web.common.DefaultRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class NotificationRouter(
    private val notificationHandler: NotificationHandler
) : DefaultRouter {
    @Bean
    fun notificationRoutes(): CoRouterFunctionDsl.() -> Unit = {
        withMemberId(::GET, notificationHandler::getNotifications)
        withMemberId(::GET, "unread-count", notificationHandler::getReadCount)
        GET("/latest", notificationHandler::getRecentNotices)
        "fcm-tokens".nest {
            withMemberId(::POST, notificationHandler::registerOrRefresh)
            withMemberId(::DELETE, notificationHandler::delete)
        }
        "send".nest {
            filter(requireRole("ROLE_ADMIN"))
            POST("/topic", notificationHandler::sendToTopic)
        }
        withMemberId(::POST, "/{notificationId}", notificationHandler::markAsRead)
    }
}