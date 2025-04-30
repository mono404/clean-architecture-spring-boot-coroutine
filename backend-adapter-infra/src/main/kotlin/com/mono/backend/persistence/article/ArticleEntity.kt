package com.mono.backend.persistence.article

import com.mono.backend.article.Article
import com.mono.backend.snowflake.Snowflake
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "article")
data class ArticleEntity(
    @Id
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = articleId
    override fun isNew(): Boolean = createdAt == null
    fun toDomain(): Article {
        return Article(
            articleId = this.articleId,
            title = this.title,
            content = this.content,
            boardId = this.boardId,
            writerId = writerId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(article: Article): ArticleEntity {
            return ArticleEntity(
                Snowflake.nextId(),
                article.title,
                article.content,
                article.boardId,
                article.writerId
            )
        }
    }
}