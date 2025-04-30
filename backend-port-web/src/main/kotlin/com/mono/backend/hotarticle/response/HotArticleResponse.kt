package com.mono.backend.hotarticle.response

import com.mono.backend.article.response.ArticleReadResponse
import java.time.LocalDateTime

data class HotArticleResponse(
    val articleId: Long,
    val title: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(articleReadResponse: ArticleReadResponse) = HotArticleResponse(
            articleId = articleReadResponse.articleId,
            title = articleReadResponse.title,
            createdAt = articleReadResponse.createdAt
        )
    }
}
