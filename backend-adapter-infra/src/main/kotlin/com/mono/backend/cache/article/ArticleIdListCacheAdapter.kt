package com.mono.backend.cache.article

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
class ArticleIdListCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): ArticleIdListCachePort {
    companion object {
        const val KET_FORMAT = "article-read::board::%s::article-list"
    }

    override suspend fun add(boardId: Long, articleId: Long, limit: Long): Long? {
        val key = generateKey(boardId)
        return redisTemplate.execute { connection ->
            val bbKey = ByteBuffer.wrap(key.toByteArray())
            val bbValue = ByteBuffer.wrap(toPaddedString(articleId).toByteArray())
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

    override suspend fun delete(boardId: Long, articleId: Long): Long? {
        return redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId)).awaitSingle()
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

    override suspend fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, limit: Long): List<Long>? {
        val range = if (lastArticleId == null) Range.unbounded()
        else Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId)))

        return redisTemplate.opsForZSet().reverseRangeByLex(
            generateKey(boardId),
            range,
            limit().count(limit.toInt())
        ).collectList().awaitSingleOrNull()?.map { it.toLong() }
    }

    private fun toPaddedString(articleId: Long): String {
        return "%019d".format(articleId)
        // 1234 -> 00000000000001234
    }

    private fun generateKey(boardId: Long): String {
        return String.format(KET_FORMAT, boardId)
    }

    suspend fun createOrUpdate(boardId: Long, map: List<Long>) {
        map.forEach { articleId ->
            delete(boardId, articleId)
            add(boardId, articleId, 1000)
        }
    }
}