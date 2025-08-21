package com.mono.backend.port.web.post.dto

import com.mono.backend.domain.common.member.EmbeddedMember
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
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,

    var postCommentCount: Long,
    var postLikeCount: Long,
    var postViewCount: Long,

    var memberId: String,
    val nickname: String,
    val profileImageUrl: String?,

    val liked: Boolean, // 좋아요 여부
) {
    companion object {
        fun from(postQueryModel: PostQueryModel, viewCount: Long, liked: Boolean): PostReadResponse {
            return PostReadResponse(
                postId = postQueryModel.postId.toString(),
                title = postQueryModel.title,
                content = postQueryModel.content,
                boardType = postQueryModel.boardType,
                createdAt = postQueryModel.createdAt,
                updatedAt = postQueryModel.updatedAt,

                postCommentCount = postQueryModel.postCommentCount,
                postLikeCount = postQueryModel.postLikeCount,
                postViewCount = viewCount,

                memberId = postQueryModel.member.memberId.toString(),
                nickname = postQueryModel.member.nickname,
                profileImageUrl = postQueryModel.member.profileImageUrl,

                liked = liked
            )
        }
    }
}

data class PostResponse(
    val postId: String,
    val title: String,
    val content: String,
    val boardType: BoardType, // 게시판 타입
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성일시
    val updatedAt: LocalDateTime = createdAt, // 수정일시

    val memberId: String, // 작성자 아이디
    val nickname: String,
    val profileImageUrl: String?,
) {
    fun toDomain(commentCount: Long, likeCount: Long): PostQueryModel {
        return PostQueryModel(
            postId = postId.toLong(),
            title = title,
            content = content,
            boardType = boardType,
            createdAt = createdAt,
            updatedAt = updatedAt,
            postCommentCount = commentCount,
            postLikeCount = likeCount,
            member = EmbeddedMember(
                memberId = memberId.toLong(),
                nickname = nickname,
                profileImageUrl = profileImageUrl
            ),
        )
    }

    companion object {
        fun from(post: Post): PostResponse = PostResponse(
            postId = post.postId.toString(),
            title = post.title,
            content = post.content,
            boardType = post.boardType,
            createdAt = post.createdAt!!,
            updatedAt = post.updatedAt!!,

            memberId = post.member.memberId.toString(),
            nickname = post.member.nickname,
            profileImageUrl = post.member.profileImageUrl
        )
    }
}