package com.mono.backend.like

import java.time.LocalDateTime

data class ArticleLike(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime? = null
) {
    companion object {
        fun from(articleLikeId: Long, articleId: Long, userId: Long): ArticleLike {
            return ArticleLike(articleLikeId, articleId, userId)
        }
    }
}