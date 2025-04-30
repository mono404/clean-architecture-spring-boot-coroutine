package com.mono.backend.persistence.like.count

import com.mono.backend.like.ArticleLikeCount
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_like_count")
data class ArticleLikeCountEntity(
    @Id
    val articleId: Long,
    var likeCount: Long,
    @Version
    val version: Long = 0,
) {
    fun toDomain(): ArticleLikeCount {
        return ArticleLikeCount(articleId = articleId, likeCount = likeCount)
    }

    companion object {
        fun from(articleLikeCount: ArticleLikeCount) = ArticleLikeCountEntity(
            articleId = articleLikeCount.articleId,
            likeCount = articleLikeCount.likeCount,
            version = articleLikeCount.version
        )
    }
}
