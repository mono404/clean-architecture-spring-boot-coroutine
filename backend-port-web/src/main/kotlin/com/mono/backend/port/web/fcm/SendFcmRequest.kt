package com.mono.backend.port.web.fcm

import com.mono.backend.domain.fcm.FcmMessage
import com.mono.backend.domain.notification.NotificationTemplate
import com.mono.backend.domain.notification.NotificationType

data class SendToMemberRequest(
    val memberId: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
) {
    fun toFcmMassage(): FcmMessage = FcmMessage(
        type = type,
        title = title,
        body = body,
        data = data ?: emptyMap()
    )

    fun toNotificationTemplate(): NotificationTemplate = NotificationTemplate(
        type = type,
        title = title,
        body = body,
        metadata = data ?: emptyMap(),
    )
}

data class SendToDeviceRequest(
    val fcmToken: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
) {
    fun toDomain(): FcmMessage = FcmMessage(
        type = type,
        title = title,
        body = body,
        data = data ?: emptyMap()
    )
}

data class SendToDevicesRequest(
    val fcmTokens: List<String>,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
) {
    fun toDomain(): FcmMessage = FcmMessage(
        type = type,
        title = title,
        body = body,
        data = data ?: emptyMap()
    )

    fun toNotificationTemplate(): NotificationTemplate = NotificationTemplate(
        type = type,
        title = title,
        body = body,
        metadata = data ?: emptyMap(),
    )
}

data class SendTopicRequest(
    val topic: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
) {
    fun toDomain(): FcmMessage = FcmMessage(
        type = type,
        title = title,
        body = body,
        data = data ?: emptyMap()
    )

    fun toNotificationTemplate(): NotificationTemplate = NotificationTemplate(
        type = type,
        title = title,
        body = body,
        metadata = data ?: emptyMap(),
    )
}