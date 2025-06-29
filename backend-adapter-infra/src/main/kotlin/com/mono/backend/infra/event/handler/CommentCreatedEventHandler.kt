package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.CommentCreatedEventPayload
import com.mono.backend.port.infra.hotpost.cache.HotPostCommentCountCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val hotPostCommentCountCachePort: HotPostCommentCountCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<CommentCreatedEventPayload> {
    private val log = logger()
    override suspend fun handleHotPost(event: Event<CommentCreatedEventPayload>) {
        event.payload?.let {
            hotPostCommentCountCachePort.createOrUpdate(
                postId = it.postId,
                commentCount = it.postCommentCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handlePostRead(event: Event<CommentCreatedEventPayload>) {
        event.payload?.let { payload ->
            try {
                postQueryModelCachePort.read(payload.postId)?.let { postQueryModel ->
                    postQueryModel.updateBy(payload)
                    postQueryModelCachePort.update(postQueryModel)
                }
            } catch (e: Exception) {
                log.error("Failed to update postQueryModel from event: $payload", e)
            }
        }
    }

    override suspend fun handleSearchIndex(event: Event<CommentCreatedEventPayload>) {
        event.payload?.let { payload ->
            val existingIndex = searchPersistencePort.findById(payload.postId) ?: return
            val updatedIndex = existingIndex.appendComment(payload.content)
            searchPersistencePort.save(updatedIndex)
        }
    }

    override suspend fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return EventType.COMMENT_CREATED == event.type
    }

    override suspend fun findPostId(event: Event<CommentCreatedEventPayload>): Long? {
        return event.payload?.postId
    }
}