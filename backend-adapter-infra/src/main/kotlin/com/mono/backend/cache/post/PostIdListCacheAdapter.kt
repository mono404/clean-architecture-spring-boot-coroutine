package com.mono.backend.cache.post

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit.limit
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.nio.ByteBuffer

@Repository
class PostIdListCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): PostIdListCachePort {
    companion object {
        const val KET_FORMAT = "post-read::board::%s::post-list"
    }

    override suspend fun add(boardId: Long, postId: Long, limit: Long): Long? {
        val key = generateKey(boardId)
        return redisTemplate.execute { connection ->
            val bbKey = ByteBuffer.wrap(key.toByteArray())
            val bbValue = ByteBuffer.wrap(toPaddedString(postId).toByteArray())
            Flux.concat(
                connection.zSetCommands().zAdd(bbKey, 0.0, bbValue),
                connection.zSetCommands().zRemRangeByRank(
                    bbKey,
                    Range.of(
                        Range.Bound.inclusive(0L),
                        Range.Bound.exclusive(-limit - 1L)
                    )
                )
            )
        }.awaitSingle()
    }

    override suspend fun delete(boardId: Long, postId: Long): Long? {
        return redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(postId)).awaitSingle()
    }

    override suspend fun readAll(boardId: Long, offset: Long, limit: Long): List<Long>? {
        return redisTemplate.opsForZSet()
            .reverseRange(
                generateKey(boardId),
                Range.of(
                    Range.Bound.inclusive(offset),
                    Range.Bound.exclusive(offset + limit - 1)
                )
            ).collectList()
            .awaitSingleOrNull()
            ?.map { it.toLong() }
    }

    override suspend fun readAllInfiniteScroll(boardId: Long, lastPostId: Long?, limit: Long): List<Long>? {
        val range = if (lastPostId == null) Range.unbounded()
        else Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastPostId)))

        return redisTemplate.opsForZSet().reverseRangeByLex(
            generateKey(boardId),
            range,
            limit().count(limit.toInt())
        ).collectList().awaitSingleOrNull()?.map { it.toLong() }
    }

    private fun toPaddedString(postId: Long): String {
        return "%019d".format(postId)
        // 1234 -> 00000000000001234
    }

    private fun generateKey(boardId: Long): String {
        return String.format(KET_FORMAT, boardId)
    }

    suspend fun createOrUpdate(boardId: Long, map: List<Long>) {
        map.forEach { postId ->
            delete(boardId, postId)
            add(boardId, postId, 1000)
        }
    }
}