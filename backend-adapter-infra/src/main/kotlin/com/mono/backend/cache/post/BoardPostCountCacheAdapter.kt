package com.mono.backend.cache.post

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardPostCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate,
): BoardPostCountCachePort {
    companion object {
        const val KET_FORMAT = "post-read::board-post-count::board::%s"
    }

    private fun generateKey(boardId: Long) = String.format(KET_FORMAT, boardId)

    override suspend fun createOrUpdate(boardId: Long, postCount: Long) {
        redisTemplate.opsForValue().set(generateKey(boardId), postCount.toString()).awaitSingle()
    }

    override suspend fun read(boardId: Long): Long? {
        return redisTemplate.opsForValue().get(generateKey(boardId)).awaitSingleOrNull()?.toLong()
    }
}