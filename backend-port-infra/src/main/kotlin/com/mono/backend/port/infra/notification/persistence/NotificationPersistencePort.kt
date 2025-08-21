package com.mono.backend.port.infra.notification.persistence

import com.mono.backend.domain.notification.Notification

interface NotificationPersistencePort {
    suspend fun save(updated: Notification): Notification
    suspend fun findByMemberId(memberId: Long, limit: Int): List<Notification>
    suspend fun findById(userNotificationId: Long): Notification?
    suspend fun deleteByTemplateIds(templateIds: List<Long>)
    suspend fun countByMemberIdAndIsReadFalse(memberId: Long): Long
}