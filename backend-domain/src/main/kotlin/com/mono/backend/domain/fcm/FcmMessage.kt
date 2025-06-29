package com.mono.backend.domain.fcm

import com.mono.backend.domain.notification.NotificationTemplate
import com.mono.backend.domain.notification.NotificationType

data class FcmMessage(
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()  // 추가 커스텀 페이로드
) {
    fun toDomain(): NotificationTemplate = NotificationTemplate(
        type = type,
        title = title,
        body = body,
        metadata = data,
    )
}
