package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import com.mono.backend.like.ArticleLike
import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long = 0,
    val articleId: Long = 0,
    val userId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val articleLikeCount: Long = 0
) : EventPayload {
    companion object {
        fun from(articleLike: ArticleLike, count: Long) = ArticleLikedEventPayload(
            articleLikeId = articleLike.articleLikeId,
            articleId = articleLike.articleId,
            userId = articleLike.userId,
            createdAt = articleLike.createdAt!!,
            articleLikeCount = count
        )
    }
}
