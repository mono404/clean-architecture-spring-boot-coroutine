package com.mono.backend.persistence.view

interface PostViewCountBackUpProcessorPort {
    suspend fun backup(postId: Long, viewCount: Long)
}