package com.mono.backend.persistence.like

import com.mono.backend.like.PostLike
import com.mono.backend.snowflake.Snowflake
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "post_like")
data class PostLikeEntity(
    @Id
    val postLikeId: Long,
    val postId: Long,
    val memberId: Long,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long = postLikeId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): PostLike {
        return PostLike(
            postLikeId = this.postLikeId,
            postId = this.postId,
            memberId = this.memberId,
            createdAt = this.createdAt,
        )
    }

    companion object {
        fun from(postId: Long, memberId: Long) = PostLikeEntity(
            postLikeId = Snowflake.nextId(),
            postId = postId,
            memberId = memberId
        )

        fun from(postLike: PostLike) = PostLikeEntity(
            postLikeId = postLike.postLikeId,
            postId = postLike.postId,
            memberId = postLike.memberId,
            createdAt = postLike.createdAt
        )
    }
}