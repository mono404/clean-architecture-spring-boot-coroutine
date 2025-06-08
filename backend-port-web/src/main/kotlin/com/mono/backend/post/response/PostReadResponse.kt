package com.mono.backend.post.response

import com.mono.backend.post.PostQueryModel
import java.time.LocalDateTime

data class PostReadResponse(
    val postId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
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
                postId = postQueryModel.postId,
                title = postQueryModel.title,
                content = postQueryModel.content,
                boardId = postQueryModel.boardId,
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