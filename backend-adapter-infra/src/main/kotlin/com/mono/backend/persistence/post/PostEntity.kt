package com.mono.backend.persistence.post

import com.mono.backend.post.Post
import com.mono.backend.snowflake.Snowflake
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "post")
data class PostEntity(
    @Id
    val postId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = postId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): Post {
        return Post(
            postId = this.postId,
            title = this.title,
            content = this.content,
            boardId = this.boardId,
            writerId = writerId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(post: Post): PostEntity {
            return PostEntity(
                Snowflake.nextId(),
                post.title,
                post.content,
                post.boardId,
                post.writerId
            )
        }
    }
}