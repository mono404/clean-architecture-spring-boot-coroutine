package com.mono.backend.event.post.handler

import com.mono.backend.cache.hotpost.HotPostViewCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostViewedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
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

    override suspend fun handlePostRead(event: Event<PostViewedEventPayload>) {
        return
    }

    override suspend fun supports(event: Event<PostViewedEventPayload>): Boolean {
        return EventType.POST_VIEWED == event.type
    }

    override suspend fun findPostId(event: Event<PostViewedEventPayload>): Long? {
        return event.payload?.postId
    }
}