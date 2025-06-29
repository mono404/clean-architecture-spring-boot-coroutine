package com.mono.backend.port.infra.notification.persistence

import com.mono.backend.domain.notification.NotificationTemplate
import com.mono.backend.domain.notification.NotificationType
import java.time.LocalDateTime

interface NotificationTemplatePersistencePort {
    suspend fun save(notificationTemplate: NotificationTemplate): NotificationTemplate
    suspend fun findByIdIn(notificationTemplateIds: List<Long>): List<NotificationTemplate>
    suspend fun deleteExpired(): Int
    suspend fun findAllByNotificationTypeAfter(
        after: LocalDateTime,
        notificationType: NotificationType
    ): List<NotificationTemplate>
}