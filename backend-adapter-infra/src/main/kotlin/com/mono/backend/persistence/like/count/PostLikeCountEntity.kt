package com.mono.backend.persistence.like.count

import com.mono.backend.like.PostLikeCount
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(name = "post_like_count")
data class PostLikeCountEntity(
    @Id
    val postId: Long,
    var likeCount: Long,
    @Version
    val version: Long = 0,
) {
    fun toDomain(): PostLikeCount {
        return PostLikeCount(postId = postId, likeCount = likeCount)
    }

    companion object {
        fun from(postLikeCount: PostLikeCount) = PostLikeCountEntity(
            postId = postLikeCount.postId,
            likeCount = postLikeCount.likeCount,
            version = postLikeCount.version
        )
    }
}
