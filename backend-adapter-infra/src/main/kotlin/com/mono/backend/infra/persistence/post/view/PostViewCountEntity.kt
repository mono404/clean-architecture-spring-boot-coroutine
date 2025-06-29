package com.mono.backend.infra.persistence.post.view

import com.mono.backend.domain.post.view.PostViewCount
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table(name = "post_view_count")
data class PostViewCountEntity(
    @Id
    val postId: Long,
    val viewCount: Long,
) : Persistable<Long> {
    override fun getId(): Long = postId
    override fun isNew(): Boolean = true
    fun toDomain(): PostViewCount {
        return PostViewCount(
            postId = postId,
            viewCount = viewCount
        )
    }

    companion object {
        fun from(postViewCount: PostViewCount): PostViewCountEntity {
            return PostViewCountEntity(
                postId = postViewCount.postId,
                viewCount = postViewCount.viewCount
            )
        }
    }
}
