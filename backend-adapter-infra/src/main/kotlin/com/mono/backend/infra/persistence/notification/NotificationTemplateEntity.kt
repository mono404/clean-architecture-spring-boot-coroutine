package com.mono.backend.infra.persistence.notification

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mono.backend.domain.notification.NotificationTemplate
import com.mono.backend.domain.notification.NotificationType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "notification_template")
data class NotificationTemplateEntity(
    @Id
    val notificationTemplateId: Long? = null,
    val type: NotificationType,
    val title: String,
    val body: String,
    val metadata: String?,
    val ttlDays: Int = 3,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long? = notificationTemplateId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain() = NotificationTemplate(
        notificationTemplateId = notificationTemplateId,
        type = type,
        title = title,
        body = body,
        metadata = ObjectMapper().readValue(metadata ?: ""),
        ttlDays = ttlDays,
        createdAt = createdAt
    )

    companion object {
        fun from(notificationTemplate: NotificationTemplate): NotificationTemplateEntity = NotificationTemplateEntity(
            notificationTemplateId = notificationTemplate.notificationTemplateId,
            type = notificationTemplate.type,
            title = notificationTemplate.title,
            body = notificationTemplate.body,
            metadata = ObjectMapper().writeValueAsString(notificationTemplate.metadata),
            ttlDays = notificationTemplate.ttlDays,
            createdAt = notificationTemplate.createdAt
        )
    }
}