package com.mono.backend.infra.cache.post

import com.mono.backend.common.log.logger
import com.mono.backend.domain.post.PostQueryModel
import com.mono.backend.infra.dataserializer.DataSerializer
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.setAndAwait
import org.springframework.data.redis.core.types.Expiration
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.nio.ByteBuffer
import java.time.Duration

@Repository
class PostQueryModelCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : PostQueryModelCachePort {
    private val log = logger()
    companion object {
        const val KEY_FORMAT = "post-read::post::%s"
    }

    override suspend fun create(postQueryModel: PostQueryModel, ttl: Duration) {
        val value = DataSerializer.serialize(postQueryModel) ?: return
        redisTemplate.opsForValue().setAndAwait(generateKey(postQueryModel), value, ttl)
    }

    override suspend fun createAll(models: List<PostQueryModel>, ttl: Duration) {
        try {
            redisTemplate.execute { connection ->
                val commands = models.mapNotNull { model ->
                    val key = generateKey(model)
                    DataSerializer.serialize(model)?.let {
                        connection.stringCommands()
                            .setEX(
                                ByteBuffer.wrap(key.toByteArray()),
                                ByteBuffer.wrap(it.toByteArray()),
                                Expiration.from(ttl),
                            )
                    }
                }
                Flux.merge(commands)
            }.awaitLast()
        } catch (e: Exception) {
            log.error("[PostQueryModelCacheAdapter.createAll] failed", e)
        }
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

    override suspend fun readAll(postIds: List<Long>): List<PostQueryModel> {
        val keyList = postIds.map(this::generateKey)
        return keyList.takeIf { it.isNotEmpty() }
            ?.let { keys -> redisTemplate.opsForValue().multiGet(keys).awaitSingleOrNull() }
            ?.mapNotNull { json -> json?.let { DataSerializer.deserialize(it, PostQueryModel::class.java) } }
            ?: emptyList()
    }
}