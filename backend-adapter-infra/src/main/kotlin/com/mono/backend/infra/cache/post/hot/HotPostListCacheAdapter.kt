package com.mono.backend.infra.cache.post.hot

import com.mono.backend.common.log.logger
import com.mono.backend.port.infra.hotpost.cache.HotPostListCachePort
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotPostListCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotPostListCachePort {
    private val log = logger()

    companion object {
        const val KEY_FORMAT = "hot-post::list::%s"
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    override suspend fun add(postId: Long?, time: LocalDateTime?, score: Double, limit: Long, ttl: Duration) {
        val key = generateKey(time)
        redisTemplate.execute { connection ->
            val bbKey = ByteBuffer.wrap(key.toByteArray())
            val bbValue = ByteBuffer.wrap(postId.toString().toByteArray())
            Flux.concat(
                connection.zSetCommands().zAdd(bbKey, score, bbValue),
                connection.zSetCommands().zRemRangeByRank(
                    bbKey, Range.of(
                        Range.Bound.inclusive(0L),
                        Range.Bound.exclusive(-limit - 1)
                    )
                ),
                connection.keyCommands().expire(bbKey, ttl)
            )
        }.awaitFirstOrNull()
    }

    override suspend fun remove(postId: Long, time: LocalDateTime) {
        redisTemplate.opsForZSet().remove(generateKey(time), postId.toString()).awaitSingleOrNull()
    }

    private fun generateKey(time: LocalDateTime?): String {
        return generateKey(TIME_FORMATTER.format(time))
    }

    private fun generateKey(dateStr: String): String {
        return String.format(KEY_FORMAT, dateStr)
    }

    override suspend fun readAll(dateStr: String): List<Long?>? {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(
                generateKey(dateStr),
                Range.of(Range.Bound.inclusive(0L), Range.Bound.inclusive(-1L))
            )
            .map {
                log.info("[HotPostListRepository.readAll] postId=${it.value}, score=${it.score}")
                it
            }
            .mapNotNull { it.value?.toLong() }
            .collectList()
            .awaitSingleOrNull()
    }
}