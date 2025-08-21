package com.mono.backend.infra.persistence.member

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.MemberRole
import com.mono.backend.domain.member.SocialProvider
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "member")
data class MemberEntity(
    @Id
    val memberId: Long,
    val providerId: String,
    val provider: SocialProvider,
    val nickname: String,
    val profileImageUrl: String?,
    val role: MemberRole,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = memberId
    override fun isNew(): Boolean = createdAt == null

    fun toDomain() = Member(
        memberId = memberId,
        providerId = providerId,
        provider = provider,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        role = role,
        createdAt = createdAt,
    )

    companion object {
        fun from(member: Member) = MemberEntity(
            memberId = member.memberId,
            providerId = member.providerId,
            provider = member.provider,
            nickname = member.nickname,
            profileImageUrl = member.profileImageUrl,
            role = member.role,
            createdAt = member.createdAt,
        )
    }
}
