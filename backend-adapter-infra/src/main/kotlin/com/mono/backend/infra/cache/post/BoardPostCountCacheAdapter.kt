package com.mono.backend.infra.cache.post

import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.post.cache.BoardPostCountCachePort
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardPostCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate,
) : BoardPostCountCachePort {
    companion object {
        const val KET_FORMAT = "post-read::board-post-count::board::%s"
    }

    private fun generateKey(boardType: BoardType) = String.format(KET_FORMAT, boardType.id)

    override suspend fun createOrUpdate(boardType: BoardType, postCount: Long) {
        redisTemplate.opsForValue().set(generateKey(boardType), postCount.toString()).awaitSingle()
    }

    override suspend fun read(boardType: BoardType): Long? {
        return redisTemplate.opsForValue().get(generateKey(boardType)).awaitSingleOrNull()?.toLong()
    }
}