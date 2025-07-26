package com.mono.backend.domain.post

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.domain.event.payload.*
import com.mono.backend.domain.post.board.BoardType
import java.time.LocalDateTime

data class PostQueryModel(
    val postId: Long = 0,
    var title: String = "",
    var content: String = "",
    var boardType: BoardType = BoardType.FREE,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var postCommentCount: Long = 0,
    var postLikeCount: Long = 0,

    // member 의 반 정규화
    val member: EmbeddedMember,
) {
    companion object {
        fun create(payload: PostCreatedEventPayload): PostQueryModel {
            return PostQueryModel(
                postId = payload.postId,
                title = payload.title,
                content = payload.content,
                boardType = payload.boardType,
                createdAt = payload.createdAt,
                updatedAt = payload.updatedAt,

                member = EmbeddedMember(
                    memberId = payload.memberId,
                    nickname = payload.nickname,
                    profileImageUrl = payload.profileImageUrl
                )
            )
        }
    }

    fun updateBy(payload: CommentCreatedEventPayload) {
        this.postCommentCount = payload.postCommentCount
    }

    fun updateBy(payload: CommentDeletedEventPayload) {
        this.postCommentCount = payload.postCommentCount
    }

    fun updateBy(payload: PostLikedEventPayload) {
        this.postLikeCount = payload.postLikeCount
    }

    fun updateBy(payload: PostUnlikedEventPayload) {
        this.postLikeCount = payload.postLikeCount
    }

    fun updateBy(payload: PostUpdatedEventPayload) {
        this.title = payload.title
        this.content = payload.content
        this.boardType = payload.boardType
        this.createdAt = payload.createdAt
        this.updatedAt = payload.updatedAt
    }
}