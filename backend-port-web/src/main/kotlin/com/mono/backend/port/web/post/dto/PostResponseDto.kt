package com.mono.backend.port.web.post.dto

import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.PostQueryModel
import com.mono.backend.domain.post.board.BoardType
import java.time.LocalDateTime

data class PostPageResponse(
    val posts: List<PostResponse>,
    val postCount: Long
)

data class PostReadPageResponse(
    val posts: List<PostReadResponse>,
    val postCount: Long
)

data class PostReadResponse(
    val postId: String,
    var title: String,
    var content: String,
    var boardType: BoardType,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var postCommentCount: Long,
    var postLikeCount: Long,
    var postViewCount: Long,
) {
    companion object {
        fun from(postQueryModel: PostQueryModel, viewCount: Long): PostReadResponse {
            return PostReadResponse(
                postId = postQueryModel.postId.toString(),
                title = postQueryModel.title,
                content = postQueryModel.content,
                boardType = postQueryModel.boardType,
                writerId = postQueryModel.writerId,
                createdAt = postQueryModel.createdAt,
                updatedAt = postQueryModel.updatedAt,
                postCommentCount = postQueryModel.postCommentCount,
                postLikeCount = postQueryModel.postLikeCount,
                postViewCount = viewCount
            )
        }
    }
}

data class PostResponse(
    val postId: String,
    val title: String,
    val content: String,
    val boardType: BoardType, // 게시판 타입
    val writerId: Long, // 작성자 아이디
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성일시
    val updatedAt: LocalDateTime = createdAt // 수정일시
) {
    fun toDomain(commentCount: Long, likeCount: Long): PostQueryModel {
        return PostQueryModel(
            postId = postId.toLong(),
            title = title,
            content = content,
            boardType = boardType,
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
                postId = post.postId.toString(),
                title = post.title,
                content = post.content,
                boardType = post.boardType,
                writerId = post.writerId,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!
            )
        }
    }
}