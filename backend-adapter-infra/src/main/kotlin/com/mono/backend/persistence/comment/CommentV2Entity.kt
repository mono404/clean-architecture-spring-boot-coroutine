package com.mono.backend.persistence.comment

import com.mono.backend.comment.CommentPath
import com.mono.backend.comment.CommentV2
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
    val articleId: Long,
    val writerId: Long,
    @Column("path")
    val commentPath: String,
    var deleted: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = commentId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): CommentV2 {
        return CommentV2(
            commentId = commentId,
            content = content,
            articleId = articleId,
            writerId = writerId,
            commentPath = CommentPath(commentPath),
            deleted = deleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(commentV2: CommentV2): CommentV2Entity {
            return CommentV2Entity(
                commentId = commentV2.commentId,
                content = commentV2.content,
                articleId = commentV2.articleId,
                writerId = commentV2.writerId,
                commentPath = commentV2.commentPath.path,
                deleted = commentV2.deleted,
                createdAt = commentV2.createdAt,
                updatedAt = commentV2.updatedAt
            )
        }
    }
}