package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.post.comment.CommentPath
import com.mono.backend.domain.post.comment.CommentV2
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "comment_v2")
data class CommentV2Entity(
    @Id
    val commentId: Long,
    val content: String,
    val postId: Long,
    @Column("path")
    val commentPath: String,
    var deleted: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    // member 의 반정규화 필드
    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String?,
) : Persistable<Long> {
    override fun getId(): Long = commentId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): CommentV2 {
        return CommentV2(
            commentId = commentId,
            content = content,
            postId = postId,
            commentPath = CommentPath(commentPath),
            deleted = deleted,
            createdAt = createdAt,
            updatedAt = updatedAt,

            member = EmbeddedMember(
                memberId = memberId,
                nickname = nickname,
                profileImageUrl = profileImageUrl
            ),
        )
    }

    companion object {
        fun from(commentV2: CommentV2): CommentV2Entity {
            return CommentV2Entity(
                commentId = commentV2.commentId,
                content = commentV2.content,
                postId = commentV2.postId,
                commentPath = commentV2.commentPath.path,
                deleted = commentV2.deleted,
                createdAt = commentV2.createdAt,

                memberId = commentV2.member.memberId,
                nickname = commentV2.member.nickname,
                profileImageUrl = commentV2.member.profileImageUrl
            )
        }
    }
}