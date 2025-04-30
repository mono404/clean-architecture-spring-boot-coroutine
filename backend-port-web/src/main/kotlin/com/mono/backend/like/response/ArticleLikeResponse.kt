package com.mono.backend.like.response

import com.mono.backend.like.ArticleLike
import java.time.LocalDateTime

data class ArticleLikeResponse(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(articleLike: ArticleLike): ArticleLikeResponse = ArticleLikeResponse(
            articleLikeId = articleLike.articleLikeId,
            articleId = articleLike.articleId,
            userId = articleLike.userId,
            createAt = articleLike.createdAt!!
        )
    }
}
