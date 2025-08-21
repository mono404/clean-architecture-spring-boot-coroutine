package com.mono.backend.domain.notification

import java.time.LocalDateTime

data class Notification(
    val notificationId: Long? = null,
    val memberId: Long,
    val notificationTemplateId: Long,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime? = null
)
