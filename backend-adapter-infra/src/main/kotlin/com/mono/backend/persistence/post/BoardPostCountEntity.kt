package com.mono.backend.persistence.post

import com.mono.backend.post.BoardPostCount
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "board_post_count")
data class BoardPostCountEntity(
    @Id
    val boardId: Long,
    val postCount: Long,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = boardId
    override fun isNew(): Boolean = createdAt == null

    fun toDomain(): BoardPostCount {
        return BoardPostCount(
            boardId = boardId,
            postCount = postCount
        )
    }

    companion object {
        fun from(boardPostCount: BoardPostCount) = BoardPostCountEntity(
            boardId = boardPostCount.boardId,
            postCount = boardPostCount.postCount
        )
    }
}
