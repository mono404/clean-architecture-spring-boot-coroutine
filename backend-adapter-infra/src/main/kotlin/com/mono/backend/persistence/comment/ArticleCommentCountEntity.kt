package com.mono.backend.persistence.comment

import com.mono.backend.comment.ArticleCommentCount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_comment_count")
data class ArticleCommentCountEntity(
    @Id
    val articleId: Long,
    val commentCount: Long,
) {
    fun toDomain() = ArticleCommentCount(
        articleId = articleId,
        commentCount = commentCount
    )

    companion object {
        fun from(articleCommentCount: ArticleCommentCount) = ArticleCommentCountEntity(
            articleId = articleCommentCount.articleId,
            commentCount = articleCommentCount.commentCount
        )
    }
}
