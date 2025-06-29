package com.mono.backend.domain.post

import com.mono.backend.domain.post.board.BoardType
import java.time.LocalDateTime

data class Post(
    val postId: Long,
    val title: String,
    val content: String,
    val boardType: BoardType, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
)