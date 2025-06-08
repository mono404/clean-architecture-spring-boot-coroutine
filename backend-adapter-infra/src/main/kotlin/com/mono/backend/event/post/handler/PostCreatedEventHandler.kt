package com.mono.backend.event.post.handler

import com.mono.backend.cache.hotpost.HotPostCreatedTimeCachePort
import com.mono.backend.cache.post.BoardPostCountCachePort
import com.mono.backend.cache.post.PostIdListCachePort
import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostCreatedEventPayload
import com.mono.backend.post.PostQueryModel
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PostCreatedEventHandler(
    private val hotPostCreatedTimeCachePort: HotPostCreatedTimeCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
) : EventHandler<PostCreatedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostCreatedEventPayload>) {
        event.payload?.let {
            hotPostCreatedTimeCachePort.createOrUpdate(
                postId = it.postId,
                createdAt = it.createdAt,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handlePostRead(event: Event<PostCreatedEventPayload>) {
        event.payload?.let { payload ->
            postQueryModelCachePort.create(
                PostQueryModel.create(payload),
                Duration.ofDays(1)
            )
            postIdListCachePort.add(payload.boardId, payload.postId, 1000L)
            boardPostCountCachePort.createOrUpdate(payload.boardId, payload.boardPostCount)
        }
    }

    override suspend fun supports(event: Event<PostCreatedEventPayload>): Boolean {
        return EventType.POST_CREATED == event.type
    }

    override suspend fun findPostId(event: Event<PostCreatedEventPayload>): Long? {
        return event.payload?.postId
    }
}