package com.mono.backend.infra.event

import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.infra.event.handler.EventHandler
import com.mono.backend.port.infra.hotpost.cache.HotPostCreatedTimeCachePort
import com.mono.backend.port.infra.hotpost.cache.HotPostListCachePort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotPostScoreUpdater(
    private val hotPostListCachePort: HotPostListCachePort,
    private val hotPostScoreCalculator: HotPostScoreCalculator,
    private val hotPostCreatedTimeCachePort: HotPostCreatedTimeCachePort
) {
    companion object {
        private const val HOT_POST_COUNT = 10L
        private val HOT_POST_TTL: Duration = Duration.ofDays(10)
    }

    suspend fun update(event: Event<EventPayload>, eventHandler: EventHandler<EventPayload>) = coroutineScope {
        val postId = eventHandler.findPostId(event)
        val createdTime = hotPostCreatedTimeCachePort.read(postId)

        if (!isPostCreatedToday(createdTime)) return@coroutineScope

        launch { eventHandler.handleHotPost(event) }

        val score = hotPostScoreCalculator.calculate(postId)
        hotPostListCachePort.add(postId, createdTime, score, HOT_POST_COUNT, HOT_POST_TTL)
    }

    private fun isPostCreatedToday(createdTime: LocalDateTime?): Boolean {
        return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now())
    }
}