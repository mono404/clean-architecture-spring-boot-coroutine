package com.mono.backend.infra.persistence.fcm

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.fcm.FcmToken
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "fcm_token")
data class FcmTokenEntity(
    @Id
    val fcmTokenId: Long? = null,
    val memberId: Long,
    val fcmToken: String,
    val deviceId: String,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long? = fcmTokenId
    override fun isNew(): Boolean = createdAt == null

    fun toDomain(): FcmToken {
        return FcmToken(
            memberId = this.memberId,
            fcmToken = this.fcmToken,
            deviceId = this.deviceId, // Assuming fcmTokenId is used as deviceId
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun from(fcmToken: FcmToken): FcmTokenEntity = FcmTokenEntity(
            fcmTokenId = Snowflake.nextId(),
            memberId = fcmToken.memberId,
            fcmToken = fcmToken.fcmToken,
            deviceId = fcmToken.deviceId,
            createdAt = fcmToken.createdAt,
            updatedAt = fcmToken.updatedAt
        )
    }
}
