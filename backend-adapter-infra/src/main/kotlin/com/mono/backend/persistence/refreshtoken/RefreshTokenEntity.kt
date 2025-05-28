package com.mono.backend.persistence.refreshtoken

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "refresh_token")
data class RefreshTokenEntity(
    @Id
    val refreshTokenId: Long? = null,
    val memberId: Long,
    val deviceId: String,

    val refreshToken: String,

    val expiresAt: Instant,
    @CreatedDate
    val createdAt: Instant? = null,
): Persistable<Long> {
    override fun getId(): Long? = refreshTokenId
    override fun isNew(): Boolean = createdAt == null
}
