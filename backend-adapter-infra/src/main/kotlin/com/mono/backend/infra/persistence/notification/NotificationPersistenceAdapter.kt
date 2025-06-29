package com.mono.backend.infra.persistence.notification

import com.mono.backend.domain.notification.Notification
import com.mono.backend.port.infra.notification.persistence.NotificationPersistencePort
import org.springframework.stereotype.Repository

@Repository
class NotificationPersistenceAdapter(
    private val notificationRepository: NotificationRepository
) : NotificationPersistencePort {
    override suspend fun findByMemberId(memberId: Long, limit: Int): List<Notification> {
        //TODO pageable
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId).map { it.toDomain() }
    }

    override suspend fun deleteByTemplateIds(templateIds: List<Long>) {
        notificationRepository.deleteByNotificationTemplateIdIn(templateIds)
    }

    override suspend fun countByMemberIdAndIsReadFalse(memberId: Long): Long {
        return notificationRepository.countByMemberIdAndIsReadIsFalse(memberId)
    }

    override suspend fun findById(userNotificationId: Long): Notification? {
        return notificationRepository.findById(userNotificationId)?.toDomain()
    }

    override suspend fun save(updated: Notification): Notification {
        return notificationRepository.save(NotificationEntity.from(updated)).toDomain()
    }
}