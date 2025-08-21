package com.mono.backend.infra.persistence.notification

import com.mono.backend.domain.notification.Notification
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "notification")
data class NotificationEntity(
    @Id
    val notificationId: Long? = null,
    val memberId: Long,
    val notificationTemplateId: Long,
    val isRead: Boolean = false,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long? = notificationId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain() = Notification(
        notificationId = notificationId,
        memberId = memberId,
        notificationTemplateId = notificationTemplateId,
        isRead = isRead,
        createdAt = createdAt
    )

    companion object {
        fun from(notification: Notification): NotificationEntity = NotificationEntity(
            notificationId = notification.notificationId,
            memberId = notification.memberId,
            notificationTemplateId = notification.notificationTemplateId,
            isRead = notification.isRead,
            createdAt = notification.createdAt
        )
    }
}
