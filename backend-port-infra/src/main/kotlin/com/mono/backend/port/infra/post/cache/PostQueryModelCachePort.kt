package com.mono.backend.port.infra.post.cache

import com.mono.backend.domain.post.PostQueryModel
import java.time.Duration

interface PostQueryModelCachePort {
    suspend fun read(postId: Long): PostQueryModel?
    suspend fun create(postQueryModel: PostQueryModel, ttl: Duration)
    suspend fun readAll(postIds: List<Long>): List<PostQueryModel>?
    suspend fun delete(postId: Long)
    suspend fun update(postQueryModel: PostQueryModel)
}