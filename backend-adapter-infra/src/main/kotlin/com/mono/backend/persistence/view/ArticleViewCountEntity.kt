package com.mono.backend.persistence.view

import com.mono.backend.view.ArticleViewCount
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_view_count")
data class ArticleViewCountEntity(
    @Id
    val articleId: Long,
    val viewCount: Long,
) : Persistable<Long> {
    override fun getId(): Long = articleId
    override fun isNew(): Boolean = true
    fun toDomain(): ArticleViewCount {
        return ArticleViewCount(
            articleId = articleId,
            viewCount = viewCount
        )
    }

    companion object {
        fun from(articleViewCount: ArticleViewCount): ArticleViewCountEntity {
            return ArticleViewCountEntity(
                articleId = articleViewCount.articleId,
                viewCount = articleViewCount.viewCount
            )
        }
    }
}
