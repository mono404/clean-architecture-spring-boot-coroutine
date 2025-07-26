package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostUpdatedEventPayload
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component

@Component
class PostUpdatedEventHandler(
    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<PostUpdatedEventPayload> {
    private val log = logger()

    override suspend fun handlePostRead(event: Event<PostUpdatedEventPayload>) {
        event.payload?.let { payload ->
            postQueryModelCachePort.read(postId = payload.postId)
                ?.let { postQueryModel ->
                    postQueryModel.updateBy(payload)
                    postQueryModelCachePort.update(postQueryModel)
                }
        }
    }

    override suspend fun handleSearchIndex(event: Event<PostUpdatedEventPayload>) {
        event.payload?.let {
            try {
                val searchIndex = it.toSearchIndex()
                searchPersistencePort.save(searchIndex)
            } catch (e: Exception) {
                log.error("Failed to update search index from event: $event", e)
            }
        }
    }

    override fun supports(event: Event<PostUpdatedEventPayload>): Boolean {
        return EventType.POST_UPDATED == event.type
    }

    override fun findPostId(event: Event<PostUpdatedEventPayload>): Long? {
        return event.payload?.postId
    }
}