package com.mono.backend.like

data class PostLikeCount(
    val postId: Long,
    var likeCount: Long,
    val version: Long = 0,
) {
    fun increase() {
        this.likeCount++
    }

    fun decrease() {
        this.likeCount--
    }
}
