package com.mono.backend.infra.event.handler

import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostViewedEventPayload
import com.mono.backend.port.infra.hotpost.cache.HotPostViewCountCachePort
import org.springframework.stereotype.Component

@Component
class PostViewEventHandler(
    private val hotPostViewCountCachePort: HotPostViewCountCachePort
) : EventHandler<PostViewedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostViewedEventPayload>) {
        event.payload?.let {
            hotPostViewCountCachePort.createOrUpdate(
                postId = it.postId,
                viewCount = it.postViewCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun supports(event: Event<PostViewedEventPayload>): Boolean {
        return EventType.POST_VIEWED == event.type
    }

    override suspend fun findPostId(event: Event<PostViewedEventPayload>): Long? {
        return event.payload?.postId
    }
}