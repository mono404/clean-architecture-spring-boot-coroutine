package com.mono.backend.infra.cache.post.hot

import com.mono.backend.port.infra.hotpost.cache.HotPostCreatedTimeCachePort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class HotPostCreatedTimeCacheAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : HotPostCreatedTimeCachePort {
    companion object {
        // hot-post::post::{postId}::created-time
        const val KEY_FORMAT = "hot-post::post::%s::created-time"
    }

    /**
     * 좋아요 이벤트가 왔는데, 이 이벤트에 대한 게시글이 오늘 게시글인지 확인하려면, 게시글 서비스에 조회가 필요합니다.
     * 하지만 게시글 생성 시간을 저장하고 있으면, 오늘 게시글인지 게시글 서비스 안찔러봐도 바로 알 수 있다.
     */
    override suspend fun createOrUpdate(postId: Long?, createdAt: LocalDateTime?, ttl: Duration) {
        redisTemplate.opsForValue()
            .set(generateKey(postId), createdAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli().toString(), ttl)
            .awaitSingleOrNull()
    }

    override suspend fun delete(postId: Long) {
        redisTemplate.delete(generateKey(postId)).awaitSingleOrNull()
    }

    override suspend fun read(postId: Long?): LocalDateTime? {
        return redisTemplate.opsForValue().get(generateKey(postId)).awaitSingleOrNull()?.let {
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it.toLong()), ZoneOffset.UTC)
        }
    }

    private fun generateKey(postId: Long?): String {
        return String.format(KEY_FORMAT, postId)
    }
}