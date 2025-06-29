package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostCreatedEventPayload
import com.mono.backend.domain.post.PostQueryModel
import com.mono.backend.port.infra.hotpost.cache.HotPostCreatedTimeCachePort
import com.mono.backend.port.infra.post.cache.BoardPostCountCachePort
import com.mono.backend.port.infra.post.cache.PostIdListCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PostCreatedEventHandler(
    private val hotPostCreatedTimeCachePort: HotPostCreatedTimeCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<PostCreatedEventPayload> {
    private val log = logger()

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
            postIdListCachePort.add(payload.boardType, payload.postId, 1000L)
            boardPostCountCachePort.createOrUpdate(payload.boardType, payload.boardPostCount)
        }
    }

    override suspend fun handleSearchIndex(event: Event<PostCreatedEventPayload>) {
        event.payload?.let {
            try {
                val searchIndex = it.toSearchIndex()
                searchPersistencePort.save(searchIndex)
            } catch (e: Exception) {
                log.error("Failed to update search index from event: $event", e)
            }
        }
    }

    override suspend fun supports(event: Event<PostCreatedEventPayload>): Boolean {
        return EventType.POST_CREATED == event.type
    }

    override suspend fun findPostId(event: Event<PostCreatedEventPayload>): Long? {
        return event.payload?.postId
    }
}