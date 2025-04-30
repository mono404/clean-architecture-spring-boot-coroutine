package com.mono.backend.article.response

import com.mono.backend.article.ArticleQueryModel
import java.time.LocalDateTime

data class ArticleReadResponse(
    val articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var articleCommentCount: Long,
    var articleLikeCount: Long,
    var articleViewCount: Long,
) {
    companion object {
        fun from(articleQueryModel: ArticleQueryModel, viewCount: Long): ArticleReadResponse {
            return ArticleReadResponse(
                articleId = articleQueryModel.articleId,
                title = articleQueryModel.title,
                content = articleQueryModel.content,
                boardId = articleQueryModel.boardId,
                writerId = articleQueryModel.writerId,
                createdAt = articleQueryModel.createdAt,
                updatedAt = articleQueryModel.updatedAt,
                articleCommentCount = articleQueryModel.articleCommentCount,
                articleLikeCount = articleQueryModel.articleLikeCount,
                articleViewCount = viewCount
            )
        }
    }
}