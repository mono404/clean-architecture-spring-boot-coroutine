package com.mono.backend.service.notification

import com.mono.backend.common.log.logger
import com.mono.backend.domain.fcm.FcmToken
import com.mono.backend.domain.notification.Notification
import com.mono.backend.domain.notification.NotificationType
import com.mono.backend.port.infra.fcm.persistence.FcmSendPort
import com.mono.backend.port.infra.fcm.persistence.FcmTokenPersistencePort
import com.mono.backend.port.infra.notification.persistence.NotificationPersistencePort
import com.mono.backend.port.infra.notification.persistence.NotificationTemplatePersistencePort
import com.mono.backend.port.web.fcm.SendToDeviceRequest
import com.mono.backend.port.web.fcm.SendToDevicesRequest
import com.mono.backend.port.web.fcm.SendToMemberRequest
import com.mono.backend.port.web.fcm.SendTopicRequest
import com.mono.backend.port.web.notification.NotificationResponse
import com.mono.backend.port.web.notification.NotificationUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationPersistencePort: NotificationPersistencePort,
    private val notificationTemplatePersistencePort: NotificationTemplatePersistencePort,
    private val fcmTokenPersistencePort: FcmTokenPersistencePort,
    private val fcmSendPort: FcmSendPort,
) : NotificationUseCase {
    private val log = logger()

    override suspend fun getRecentNotifications(memberId: Long, limit: Int): List<NotificationResponse> {
        val notifications = notificationPersistencePort.findByMemberId(memberId, limit)
        val templates = notificationTemplatePersistencePort.findByIdIn(notifications.map { it.notificationTemplateId })
            .associateBy { it.notificationTemplateId }
        return notifications.mapNotNull {
            templates[it.notificationTemplateId]?.let { t ->
                NotificationResponse(
                    notificationId = it.notificationId!!,
                    title = t.title,
                    body = t.body,
                    isRead = it.isRead,
                    createdAt = it.createdAt!!
                )
            }
        }
    }

    override suspend fun markAsRead(memberId: Long, userNotificationId: Long): Boolean {
        val notification = notificationPersistencePort.findById(userNotificationId) ?: return false
        if (notification.memberId != memberId) return false
        if (notification.isRead) return true

        val updated = notification.copy(isRead = true)
        notificationPersistencePort.save(updated)
        return true
    }

    override suspend fun registerOrRefresh(memberId: Long, fcmToken: String, deviceId: String): FcmToken {
        val token = FcmToken(
            memberId = memberId,
            fcmToken = fcmToken,
            deviceId = deviceId
        )
        return fcmTokenPersistencePort.upsert(token)
    }

    override suspend fun delete(memberId: Long, deviceId: String): Boolean {
        return fcmTokenPersistencePort.delete(memberId, deviceId)
    }

    override suspend fun sendToMember(message: SendToMemberRequest): List<String> {
        val template = notificationTemplatePersistencePort.save(message.toNotificationTemplate())
        notificationPersistencePort.save(
            Notification(
                memberId = message.memberId,
                notificationTemplateId = template.notificationTemplateId!!
            )
        )

        val tokens = fcmTokenPersistencePort.findAllByMemberId(message.memberId).map { it.fcmToken }
        if (tokens.isEmpty()) throw IllegalArgumentException("No FCM tokens found for memberId: ${message.memberId}")
        return sendToDevices(
            SendToDevicesRequest(
                fcmTokens = tokens,
                type = message.type,
                title = message.title,
                body = message.body,
                data = message.data
            )
        )
    }

    private suspend fun sendToDevice(message: SendToDeviceRequest): String {
        return fcmSendPort.sendToDevice(message.fcmToken, message.toDomain())
    }

    private suspend fun sendToDevices(message: SendToDevicesRequest): List<String> {
        return fcmSendPort.sendToDevices(message.fcmTokens, message.toDomain())
    }

    override suspend fun sendToTopic(message: SendTopicRequest): String {
        notificationTemplatePersistencePort.save(message.toNotificationTemplate())
        return fcmSendPort.sendToTopic(message.topic, message.toDomain())
    }

    override suspend fun getUnreadCount(memberId: Long): Long {
        return notificationPersistencePort.countByMemberIdAndIsReadFalse(memberId)
    }

    /**
     * 토큰을 이용한 공지 푸시
     * NotiTemplate 저장 및 유저별 관리 Noti 저장
     * async 와 chunk 처리 중요
     * 로그와 통계 분석이 가능, 전송 실패 시 재전송이 가능
     * 유저 수 X 알림 수 만큼 DB write 발생
     */
    suspend fun sendAllMemberNoticeUsingToken(message: SendToDevicesRequest) {
        val template = notificationTemplatePersistencePort.save(message.toNotificationTemplate())

        val memberIds = fcmTokenPersistencePort.findAllMemberIdsWithToken()

        coroutineScope {
            memberIds.chunked(100).map { chunk ->
                launch {
                    for (memberId in chunk) {
                        val tokens = getTokensByMemberId(memberId)
                        if (tokens.isEmpty()) continue

                        notificationPersistencePort.save(
                            Notification(
                                memberId = memberId,
                                notificationTemplateId = template.notificationTemplateId!!,
                            )
                        )

                        try {
                            fcmSendPort.sendToDevices(tokens, message.toDomain())
                        } catch (e: Exception) {
                            // 전송 실패는 무시
                            log.error("FCM 전송 실패: member_id-$memberId - ${e.message}")
                        }
                    }
                }
            }.joinAll() // 모든 코루틴이 끝날 때까지 기다림
        }

        log.info("전체 회원 대상 공지 푸시 완료 (${memberIds.size}명)")
    }

    /**
     * 토픽을 이용한 공지 푸시
     * sendToTopic("all") 호출
     * NotiTemplate 만 저장
     * getRecentNotices 로 누락 보완 필요
     */
    override suspend fun getRecentNotices(after: LocalDateTime): List<NotificationResponse> {
        return notificationTemplatePersistencePort.findAllByNotificationTypeAfter(after, NotificationType.SYSTEM_NOTICE)
            .map { NotificationResponse.from(it) }
    }

    private suspend fun getTokensByMemberId(memberId: Long): List<String> {
        return fcmTokenPersistencePort.findAllByMemberId(memberId).map { it.fcmToken }
    }

    /**
     * for batch
     * Scheduled(cron = "0 0 3 * * *")
     * */
    suspend fun deleteExpiredNotifications() {
        val deleted = notificationTemplatePersistencePort.deleteExpired()
        log.info("Deleted expired notifications: $deleted")
    }

}