package com.mono.backend.infra.cache.search.hot

import com.mono.backend.common.log.logger
import com.mono.backend.domain.search.hot.HotKeywordScope
import com.mono.backend.port.infra.search.cache.hot.HotKeywordCachePort
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class HotKeywordCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotKeywordCachePort {
    private val log = logger()

    companion object {
        const val KEY_FORMAT = "hot-keyword::list::%s::%s::%s"
        const val LIMIT = 10L
    }

    override suspend fun increaseKeywordScore(type: String, keyword: String, scope: HotKeywordScope) {
        val key = generateKey(type, scope)

        redisTemplate.opsForZSet()
            .incrementScore(key, keyword, 1.0)
            .awaitSingle()

        redisTemplate.expire(key, scope.ttl)
            .awaitSingle()
    }

    override suspend fun getTopKeywords(type: String, scope: HotKeywordScope): List<String> {
        val key = generateKey(type, scope)
        val range = Range.closed(0, LIMIT - 1)
        return redisTemplate.opsForZSet()
            .reverseRange(key, range)
            .collectList()
            .awaitSingleOrNull() ?: emptyList()
    }

    private fun generateKey(type: String, scope: HotKeywordScope): String {
        return String.format(KEY_FORMAT, type, scope.name.lowercase(), scope.todayKeyPrefix(type))
    }
}