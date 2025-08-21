package com.mono.backend.port.infra.view.persistence

interface PostViewCountBackUpProcessorPort {
    suspend fun backup(postId: Long, viewCount: Long)
}