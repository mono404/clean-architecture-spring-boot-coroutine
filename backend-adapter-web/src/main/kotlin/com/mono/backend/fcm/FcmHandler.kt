package com.mono.backend.fcm

import com.mono.backend.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody

@Component
class FcmHandler(
    private val fcmUseCase: FcmUseCase,
    private val sendFcmUseCase: SendFcmUseCase
) : DefaultHandler {
    suspend fun registerOrRefresh(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val fcmTokenRequest = serverRequest.awaitBody<FcmTokenRequest>()
        val fcmToken = fcmUseCase.registerOrRefresh(memberId, fcmTokenRequest.fcmToken, fcmTokenRequest.deviceId)
        return created("", fcmToken)
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val fcmTokenRequest = serverRequest.awaitBody<FcmTokenRequest>()
        fcmUseCase.delete(memberId, fcmTokenRequest.deviceId)
        return noContent()
    }

    suspend fun sendToDevice(serverRequest: ServerRequest): ServerResponse {
        val fcmRequest = serverRequest.awaitBody<SendToDeviceRequest>()
        val fcmMessage = FcmMessage(
            title = fcmRequest.title,
            body = fcmRequest.body,
            data = fcmRequest.data ?: emptyMap(),
        )
        val messageId = sendFcmUseCase.sendToDevice(fcmRequest.fcmToken, fcmMessage)
        return ok(messageId)
    }

    suspend fun sendMulticast(serverRequest: ServerRequest): ServerResponse {
        val fcmRequest = serverRequest.awaitBody<SendMulticastRequest>()
        val fcmMessage = FcmMessage(
            title = fcmRequest.title,
            body = fcmRequest.body,
            data = fcmRequest.data ?: emptyMap(),
        )
        val messageIds = sendFcmUseCase.sendToMulticast(fcmRequest.fcmTokens, fcmMessage)
        return ok(messageIds)
    }

    suspend fun sendToTopic(serverRequest: ServerRequest): ServerResponse {
        val fcmRequest = serverRequest.awaitBody<SendTopicRequest>()
        val fcmMessage = FcmMessage(
            title = fcmRequest.title,
            body = fcmRequest.body,
            data = fcmRequest.data ?: emptyMap(),
        )
        val messageId = sendFcmUseCase.sendToTopic(fcmRequest.topic, fcmMessage)
        return ok(messageId)
    }
}