package com.mono.backend.persistence.like

import com.mono.backend.like.ArticleLike
import com.mono.backend.snowflake.Snowflake
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "article_like")
data class ArticleLikeEntity(
    @Id
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long = articleLikeId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): ArticleLike {
        return ArticleLike(
            articleLikeId = this.articleLikeId,
            articleId = this.articleId,
            userId = this.userId,
            createdAt = this.createdAt,
        )
    }

    companion object {
        fun from(articleId: Long, userId: Long) = ArticleLikeEntity(
            articleLikeId = Snowflake.nextId(),
            articleId = articleId,
            userId = userId
        )

        fun from(articleLike: ArticleLike) = ArticleLikeEntity(
            articleLikeId = articleLike.articleLikeId,
            articleId = articleLike.articleId,
            userId = articleLike.userId,
            createdAt = articleLike.createdAt
        )
    }
}