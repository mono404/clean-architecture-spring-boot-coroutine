package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostCreatedEventPayload
import com.mono.backend.domain.post.PostQueryModel
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.hotpost.cache.HotPostCreatedTimeCachePort
import com.mono.backend.port.infra.post.cache.BoardPostCountCachePort
import com.mono.backend.port.infra.post.cache.PostIdListCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

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

    override suspend fun handlePostRead(event: Event<PostCreatedEventPayload>): Unit = coroutineScope {
        event.payload?.let { payload ->
            launch { postQueryModelCachePort.create(PostQueryModel.create(payload)) }
            launch {
                postIdListCachePort.save(payload.boardType, payload.postId)
                postIdListCachePort.save(BoardType.ALL, payload.postId)
            }
            launch { boardPostCountCachePort.createOrUpdate(payload.boardType, payload.boardPostCount) }
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

    override fun supports(event: Event<PostCreatedEventPayload>): Boolean {
        return EventType.POST_CREATED == event.type
    }

    override fun findPostId(event: Event<PostCreatedEventPayload>): Long? {
        return event.payload?.postId
    }
}