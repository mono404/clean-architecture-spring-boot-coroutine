package com.mono.backend.infra.persistence.notification

import com.mono.backend.domain.notification.NotificationTemplate
import com.mono.backend.domain.notification.NotificationType
import com.mono.backend.port.infra.notification.persistence.NotificationTemplatePersistencePort
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class NotificationTemplatePersistenceAdapter(
    private val notificationTemplateRepository: NotificationTemplateRepository
) : NotificationTemplatePersistencePort {
    override suspend fun save(notificationTemplate: NotificationTemplate): NotificationTemplate {
        return notificationTemplateRepository.save(NotificationTemplateEntity.from(notificationTemplate)).toDomain()
    }

    override suspend fun findByIdIn(notificationTemplateIds: List<Long>): List<NotificationTemplate> {
        return notificationTemplateRepository.findAllByIdIn(notificationTemplateIds).map { it.toDomain() }
    }

    override suspend fun deleteExpired(): Int {
        return notificationTemplateRepository.deleteExpired()
    }

    override suspend fun findAllByNotificationTypeAfter(
        after: LocalDateTime,
        notificationType: NotificationType
    ): List<NotificationTemplate> {
        return notificationTemplateRepository.findAllByTypeAndCreatedAtAfter(notificationType, after)
            .map { it.toDomain() }
    }
}