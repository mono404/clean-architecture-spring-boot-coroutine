package com.mono.backend.post.response

import com.mono.backend.post.Post
import com.mono.backend.post.PostQueryModel
import java.time.LocalDateTime

data class PostResponse(
    val postId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성일시
    val updatedAt: LocalDateTime = createdAt // 수정일시
) {
    fun toDomain(commentCount: Long, likeCount: Long): PostQueryModel {
        return PostQueryModel(
            postId = postId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            postCommentCount = commentCount,
            postLikeCount = likeCount
        )
    }

    companion object {
        fun from(post: Post): PostResponse {
            return PostResponse(
                postId = post.postId,
                title = post.title,
                content = post.content,
                boardId = post.boardId,
                writerId = post.writerId,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!
            )
        }
    }
}