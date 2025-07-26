package com.mono.backend.domain.post.like

data class PostLikeCount(
    val postId: Long,
    var likeCount: Long,
    var version: Long = 0,
) {
    fun increase() {
        this.likeCount++
        this.version++
    }

    fun decrease() {
        this.likeCount--
        this.version++
    }
}
