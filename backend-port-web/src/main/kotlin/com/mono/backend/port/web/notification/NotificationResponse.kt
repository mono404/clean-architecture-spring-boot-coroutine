package com.mono.backend.port.web.notification

import com.mono.backend.domain.notification.NotificationTemplate
import java.time.LocalDateTime

data class NotificationResponse(
    val notificationId: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(notificationTemplate: NotificationTemplate) = NotificationResponse(
            notificationId = notificationTemplate.notificationTemplateId!!,
            title = notificationTemplate.title,
            body = notificationTemplate.body,
            isRead = false,
            createdAt = notificationTemplate.createdAt!!
        )
    }
}
