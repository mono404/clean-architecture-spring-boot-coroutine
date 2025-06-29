package com.mono.backend.infra.persistence.post.comment

import com.mono.backend.domain.post.comment.PostCommentCount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "post_comment_count")
data class PostCommentCountEntity(
    @Id
    val postId: Long,
    val commentCount: Long,
) {
    fun toDomain() = PostCommentCount(
        postId = postId,
        commentCount = commentCount
    )

    companion object {
        fun from(postCommentCount: PostCommentCount) = PostCommentCountEntity(
            postId = postCommentCount.postId,
            commentCount = postCommentCount.commentCount
        )
    }
}
