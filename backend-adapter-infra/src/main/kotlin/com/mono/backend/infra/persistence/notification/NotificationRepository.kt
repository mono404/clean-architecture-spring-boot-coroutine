package com.mono.backend.infra.persistence.notification

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : CoroutineCrudRepository<NotificationEntity, Long> {
    suspend fun findByMemberIdOrderByCreatedAtDesc(memberId: Long): List<NotificationEntity>
    suspend fun deleteByNotificationTemplateIdIn(notificationTemplateIds: List<Long>)
    suspend fun countByMemberIdAndIsReadIsFalse(memberId: Long): Long
}