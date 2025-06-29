package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.post.comment.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "comment")
data class CommentEntity(
    @Id
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val postId: Long,
    val writerId: Long,
    var deleted: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = commentId
    override fun isNew(): Boolean = createdAt == null

    companion object {
        fun from(comment: Comment): CommentEntity {
            return CommentEntity(
                commentId = comment.commentId,
                content = comment.content,
                parentCommentId = comment.parentCommentId,
                postId = comment.postId,
                writerId = comment.writerId
            )
        }
    }

    fun toDomain(): Comment {
        return Comment(
            commentId = commentId,
            content = content,
            parentCommentId = parentCommentId,
            postId = postId,
            writerId = writerId
        )
    }
}
