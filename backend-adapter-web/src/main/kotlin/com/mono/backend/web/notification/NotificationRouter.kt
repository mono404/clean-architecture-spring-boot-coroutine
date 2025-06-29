package com.mono.backend.web.notification

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class NotificationRouter(
    private val notificationHandler: NotificationHandler
) {
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

    private fun requireRole(role: String): suspend (ServerRequest, suspend (ServerRequest) -> ServerResponse) -> ServerResponse {
        return { request, next ->
            val roles = request.attribute("roles").orElse(emptyList<String>()) as List<String>

            if (role in roles) {
                next(request)
            } else {
                ServerResponse.status(403).bodyValueAndAwait("Forbidden: $role required")
            }
        }
    }

    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        f: suspend (Long, ServerRequest) -> ServerResponse
    ) = withMemberId(function, "", f)

    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        pattern: String,
        f: suspend (Long, ServerRequest) -> ServerResponse
    ) {
        return function(pattern) { serverRequest ->
            val memberId = serverRequest.attribute("memberId").get() as Long
            f(memberId, serverRequest)
        }
    }
}