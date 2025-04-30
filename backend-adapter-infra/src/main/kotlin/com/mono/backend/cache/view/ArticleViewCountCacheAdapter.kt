package com.mono.backend.cache.view

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate,
): ArticleViewCountCachePort {
    companion object {
        // view::article::{articleId}::view_count
        const val KET_FORMAT = "view::article::%s::view_count"
    }

    override suspend fun read(articleId: Long): Long = redisTemplate.opsForValue()
        .get(generateKey(articleId))
        .awaitSingleOrNull()?.toLong()
        ?: 0

    override suspend fun increase(articleId: Long): Long? {
        return redisTemplate.opsForValue().increment(generateKey(articleId)).awaitSingleOrNull()
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KET_FORMAT, articleId)
    }
}