package com.mono.backend.persistence.article

import com.mono.backend.article.BoardArticleCount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "board_article_count")
data class BoardArticleCountEntity(
    @Id
    val boardId: Long,
    val articleCount: Long
) {
    fun toDomain(): BoardArticleCount {
        return BoardArticleCount(
            boardId = boardId,
            articleCount = articleCount
        )
    }

    companion object {
        fun from(boardArticleCount: BoardArticleCount) = BoardArticleCountEntity(
            boardId = boardArticleCount.boardId,
            articleCount = boardArticleCount.articleCount
        )
    }
}
