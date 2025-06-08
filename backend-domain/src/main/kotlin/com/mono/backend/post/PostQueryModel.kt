package com.mono.backend.post

import com.mono.backend.event.payload.*
import java.time.LocalDateTime

data class PostQueryModel(
    val postId: Long = 0,
    var title: String = "",
    var content: String = "",
    var boardId: Long = 0,
    var writerId: Long = 0,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var postCommentCount: Long = 0,
    var postLikeCount: Long = 0,
) {
    companion object {
        fun create(payload: PostCreatedEventPayload): PostQueryModel {
            return PostQueryModel(
                postId = payload.postId,
                title = payload.title,
                content = payload.content,
                boardId = payload.boardId,
                writerId = payload.writerId,
                createdAt = payload.createdAt,
                updatedAt = payload.updatedAt,
                postCommentCount = 0L,
                postLikeCount = 0L
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
        this.boardId = payload.boardId
        this.writerId = payload.writerId
        this.createdAt = payload.createdAt
        this.updatedAt = payload.updatedAt
    }
}