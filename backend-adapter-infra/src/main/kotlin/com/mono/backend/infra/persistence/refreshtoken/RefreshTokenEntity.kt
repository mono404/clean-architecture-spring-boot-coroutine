package com.mono.backend.infra.persistence.refreshtoken

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "refresh_token")
data class RefreshTokenEntity(
    @Id
    val refreshTokenId: Long? = null,
    val memberId: Long,
    val deviceId: String,

    val refreshToken: String,

    val expiresAt: LocalDateTime,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
): Persistable<Long> {
    override fun getId(): Long? = refreshTokenId
    override fun isNew(): Boolean = createdAt == null
}
