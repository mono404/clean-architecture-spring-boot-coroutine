package com.mono.backend.cache.post

import com.mono.backend.dataserializer.DataSerializer
import com.mono.backend.post.PostQueryModel
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class PostQueryModelCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : PostQueryModelCachePort {
    companion object {
        const val KEY_FORMAT = "post-read::post::%s"
    }

    override suspend fun create(postQueryModel: PostQueryModel, ttl: Duration) {
        val value = DataSerializer.serialize(postQueryModel) ?: return
        redisTemplate.opsForValue().set(generateKey(postQueryModel), value, ttl).awaitSingle()
    }

    override suspend fun update(postQueryModel: PostQueryModel) {
        val value = DataSerializer.serialize(postQueryModel) ?: return
        redisTemplate.opsForValue().setIfPresent(generateKey(postQueryModel), value).awaitSingle()
    }

    override suspend fun delete(postId: Long) {
        redisTemplate.delete(generateKey(postId)).awaitSingle()
    }

    override suspend fun read(postId: Long): PostQueryModel? {
        return redisTemplate.opsForValue().get(generateKey(postId)).awaitSingleOrNull()
            ?.let { json -> DataSerializer.deserialize(json, PostQueryModel::class.java) }
    }

    private fun generateKey(postQueryModel: PostQueryModel): String {
        return generateKey(postQueryModel.postId)
    }

    private fun generateKey(postId: Long): String {
        return String.format(KEY_FORMAT, postId)
    }

    override suspend fun readAll(postIds: List<Long>): List<PostQueryModel>? {
        val keyList = postIds.map(this::generateKey)
        return keyList.takeIf { it.isNotEmpty() }
            ?.let { keys -> redisTemplate.opsForValue().multiGet(keys).awaitSingleOrNull() }
            ?.mapNotNull { json -> json?.let { DataSerializer.deserialize(it, PostQueryModel::class.java) } }
    }
}