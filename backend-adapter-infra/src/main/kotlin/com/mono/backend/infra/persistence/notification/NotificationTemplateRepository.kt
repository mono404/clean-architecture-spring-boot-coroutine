package com.mono.backend.infra.persistence.notification

import com.mono.backend.domain.notification.NotificationType
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface NotificationTemplateRepository : CoroutineCrudRepository<NotificationTemplateEntity, Long> {
    suspend fun findAllByIdIn(ids: List<Long>): List<NotificationTemplateEntity>

    @Query(
        """
        DELETE FROM user_notification u
        JOIN notification_templage t ON u.template_id = t.id
        WHERE DATE_ADD(t.created_at, INTERVAL t.ttl_days DAY) < NOW()
    """
    )
    suspend fun deleteExpired(): Int
    suspend fun findAllByTypeAndCreatedAtAfter(
        type: NotificationType,
        createdAt: LocalDateTime
    ): List<NotificationTemplateEntity>
}