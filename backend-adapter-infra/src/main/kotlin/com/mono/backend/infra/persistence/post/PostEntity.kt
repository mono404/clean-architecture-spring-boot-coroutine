package com.mono.backend.infra.persistence.post

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
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
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    // member 의 반정규화 필드
    val memberId: Long, // 작성자 아이디
    val nickname: String,
    val profileImageUrl: String?
) : Persistable<Long> {
    override fun getId(): Long = postId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): Post {
        return Post(
            postId = postId,
            title = title,
            content = content,
            boardType = BoardType.fromId(boardId),
            createdAt = createdAt,
            updatedAt = updatedAt,

            member = EmbeddedMember(
                memberId = memberId,
                nickname = nickname,
                profileImageUrl = profileImageUrl
            )
        )
    }

    companion object {
        fun from(post: Post): PostEntity {
            return PostEntity(
                postId = Snowflake.nextId(),
                title = post.title,
                content = post.content,
                boardId = post.boardType.id,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,

                memberId = post.member.memberId,
                nickname = post.member.nickname,
                profileImageUrl = post.member.profileImageUrl,
            )
        }
    }
}