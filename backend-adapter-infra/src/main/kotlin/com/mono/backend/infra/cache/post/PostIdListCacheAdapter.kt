package com.mono.backend.infra.cache.post

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.post.cache.PostIdListCachePort
import kotlinx.coroutines.reactive.awaitFirstOrNull
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
) : PostIdListCachePort {
    companion object {
        const val KET_FORMAT = "post-read::board::%s::post-list"
    }

    override suspend fun save(boardType: BoardType, postId: Long, limit: Long): Long? {
        val key = generateKey(boardType)
        val paddedId = toPaddedString(postId).toByteArray()
        return redisTemplate.execute { connection ->
            val bbKey = ByteBuffer.wrap(key.toByteArray())
            val bbValue = ByteBuffer.wrap(paddedId)
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
        }.awaitFirstOrNull()
    }

    override suspend fun saveAll(boardType: BoardType, postIds: List<Long>, limit: Long) {
        if (postIds.isEmpty()) return

        val key = generateKey(boardType)
        val paddedIds = postIds.map { toPaddedString(it) }

        redisTemplate.execute { connection ->
            val bbKey = ByteBuffer.wrap(key.toByteArray())

            val zAddOps = paddedIds.map {
                val bbValue = ByteBuffer.wrap(it.toByteArray())
                connection.zSetCommands().zAdd(bbKey, 0.0, bbValue)
            }

            val trimOp = connection.zSetCommands().zRemRangeByRank(
                bbKey,
                Range.of(
                    Range.Bound.inclusive(0L),
                    Range.Bound.exclusive(-limit - 1L)
                )
            )

            Flux.concat(zAddOps + trimOp)
        }.then().awaitSingleOrNull()
    }

    override suspend fun delete(boardType: BoardType, postId: Long): Long? {
        return redisTemplate.opsForZSet().remove(generateKey(boardType), toPaddedString(postId)).awaitSingle()
    }

    override suspend fun readAll(boardType: BoardType, pageRequest: PageRequest): List<Long>? {
        val offset = (pageRequest.page - 1) * pageRequest.size
        val limit = offset + pageRequest.size - 1
        return redisTemplate.opsForZSet()
            .reverseRange(
                generateKey(boardType),
                Range.of(
                    Range.Bound.inclusive(offset),
                    Range.Bound.exclusive(limit)
                )
            ).collectList()
            .awaitSingleOrNull()
            ?.map { it.toLong() }
    }

    override suspend fun readAllInfiniteScroll(boardType: BoardType, cursorRequest: CursorRequest): List<Long>? {
        val range = if (cursorRequest.cursor == null) Range.unbounded()
        else Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(cursorRequest.cursor!!.toLong())))

        return redisTemplate.opsForZSet().reverseRangeByLex(
            generateKey(boardType),
            range,
            limit().count(cursorRequest.size.toInt())
        ).collectList().awaitSingleOrNull()?.map { it.toLong() }
    }

    private fun toPaddedString(postId: Long): String {
        return "%019d".format(postId)
        // 1234 -> 00000000000001234
    }

    private fun generateKey(boardType: BoardType): String {
        return String.format(
            KET_FORMAT,
            boardType.id
        )
    }
}