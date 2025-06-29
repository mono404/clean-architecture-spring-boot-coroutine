package com.mono.backend.port.web.notification

import com.mono.backend.domain.fcm.FcmToken
import com.mono.backend.port.web.fcm.SendToMemberRequest
import com.mono.backend.port.web.fcm.SendTopicRequest
import java.time.LocalDateTime

interface NotificationUseCase {
    /** Member UseCase */
    suspend fun getRecentNotifications(memberId: Long, limit: Int): List<NotificationResponse>
    suspend fun markAsRead(memberId: Long, userNotificationId: Long): Boolean
    suspend fun registerOrRefresh(memberId: Long, fcmToken: String, deviceId: String): FcmToken
    suspend fun delete(memberId: Long, deviceId: String): Boolean

    /** Admin UseCase*/
    suspend fun sendToMember(message: SendToMemberRequest): List<String>
    suspend fun sendToTopic(message: SendTopicRequest): String
    suspend fun getUnreadCount(memberId: Long): Long
    suspend fun getRecentNotices(after: LocalDateTime): List<NotificationResponse>
}