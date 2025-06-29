package com.mono.backend.web.notification

import com.mono.backend.port.web.fcm.FcmTokenRequest
import com.mono.backend.port.web.fcm.SendToMemberRequest
import com.mono.backend.port.web.fcm.SendTopicRequest
import com.mono.backend.port.web.notification.NotificationUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.time.LocalDateTime

@Component
class NotificationHandler(
    private val notificationUseCase: NotificationUseCase
) : DefaultHandler {
    /** Member UseCase */
    suspend fun getNotifications(memberId: Long, serverRequest: ServerRequest): ServerResponse {
        return ok(notificationUseCase.getRecentNotifications(memberId, limit = 30))
    }

    suspend fun markAsRead(memberId: Long, serverRequest: ServerRequest): ServerResponse {
        val notificationId = serverRequest.pathVariable("notificationId").toLong()
        return notificationUseCase.markAsRead(memberId, notificationId).takeIf { it }?.let { ok("true") } ?: notFound()
    }

    suspend fun registerOrRefresh(memberId: Long, serverRequest: ServerRequest): ServerResponse {
        val fcmTokenRequest = serverRequest.awaitBody<FcmTokenRequest>()
        val fcmToken =
            notificationUseCase.registerOrRefresh(memberId, fcmTokenRequest.fcmToken, fcmTokenRequest.deviceId)
        return created("", fcmToken)
    }

    // TODO 프론트 로그아웃 시 토큰 삭제
    suspend fun delete(memberId: Long, serverRequest: ServerRequest): ServerResponse {
        val fcmTokenRequest = serverRequest.awaitBody<FcmTokenRequest>()
        notificationUseCase.delete(memberId, fcmTokenRequest.deviceId)
        return noContent()
    }

    suspend fun getReadCount(memberId: Long, serverRequest: ServerRequest): ServerResponse {
        val count = notificationUseCase.getUnreadCount(memberId)
        return ok(count)
    }

    suspend fun getRecentNotices(serverRequest: ServerRequest): ServerResponse {
        val after = serverRequest.queryParamOrNull("after")?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
            .minusDays(3)

        return ok(notificationUseCase.getRecentNotices(after))
    }

    /** Admin UseCase*/
    suspend fun sendToMember(serverRequest: ServerRequest): ServerResponse {
        val fcmRequest = serverRequest.awaitBody<SendToMemberRequest>()
        val messageId = notificationUseCase.sendToMember(fcmRequest)
        return ok(messageId)
    }

    suspend fun sendToTopic(serverRequest: ServerRequest): ServerResponse {
        val fcmRequest = serverRequest.awaitBody<SendTopicRequest>()
        val messageId = notificationUseCase.sendToTopic(fcmRequest)
        return ok(messageId)
    }
}