package com.mono.backend.domain.notification

import java.time.LocalDateTime

data class NotificationTemplate(
    val notificationTemplateId: Long? = null,
    val type: NotificationType,
    val title: String,
    val body: String,
    val metadata: Map<String, String>,
    val ttlDays: Int = 3,
    val createdAt: LocalDateTime? = null
)