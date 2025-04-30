package com.mono.backend.article

import java.time.LocalDateTime

data class Article(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
)