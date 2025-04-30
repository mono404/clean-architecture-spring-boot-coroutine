package com.mono.backend.article

import com.mono.backend.event.payload.*
import java.time.LocalDateTime

data class ArticleQueryModel(
    val articleId: Long = 0,
    var title: String = "",
    var content: String = "",
    var boardId: Long = 0,
    var writerId: Long = 0,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var articleCommentCount: Long = 0,
    var articleLikeCount: Long = 0,
) {
    companion object {
        fun create(payload: ArticleCreatedEventPayload): ArticleQueryModel {
            return ArticleQueryModel(
                articleId = payload.articleId,
                title = payload.title,
                content = payload.content,
                boardId = payload.boardId,
                writerId = payload.writerId,
                createdAt = payload.createdAt,
                updatedAt = payload.updatedAt,
                articleCommentCount = 0L,
                articleLikeCount = 0L
            )
        }
    }

    fun updateBy(payload: CommentCreatedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: CommentDeletedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: ArticleLikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUnlikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUpdatedEventPayload) {
        this.title = payload.title
        this.content = payload.content
        this.boardId = payload.boardId
        this.writerId = payload.writerId
        this.createdAt = payload.createdAt
        this.updatedAt = payload.updatedAt
    }
}