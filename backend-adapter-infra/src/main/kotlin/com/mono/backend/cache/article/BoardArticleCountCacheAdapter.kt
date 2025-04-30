package com.mono.backend.cache.article

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardArticleCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate,
): BoardArticleCountCachePort {
    companion object {
        const val KET_FORMAT = "article-read::board-article-count::board::%s"
    }

    private fun generateKey(boardId: Long) = String.format(KET_FORMAT, boardId)

    override suspend fun createOrUpdate(boardId: Long, articleCount: Long) {
        redisTemplate.opsForValue().set(generateKey(boardId), articleCount.toString()).awaitSingle()
    }

    override suspend fun read(boardId: Long): Long? {
        return redisTemplate.opsForValue().get(generateKey(boardId)).awaitSingleOrNull()?.toLong()
    }
}