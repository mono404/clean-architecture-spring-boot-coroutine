package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.CommentUpdatedEventPayload
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePortV2
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component

@Component
class CommentUpdatedEventHandler(
    private val commentPersistencePortV2: CommentPersistencePortV2,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<CommentUpdatedEventPayload> {
    private val log = logger()

    /** Full Re-Aggregation 방식 */
    override suspend fun handleSearchIndex(event: Event<CommentUpdatedEventPayload>) {
        event.payload?.let { payload ->
            val allComments = commentPersistencePortV2.findAllByPostId(payload.postId)

            val combinedText = allComments.joinToString(" ") { it.content }

            val existing = searchPersistencePort.findById(payload.postId) ?: return
            val updated = existing.copy(comment = combinedText)

            searchPersistencePort.save(updated)
        }
    }

    override fun supports(event: Event<CommentUpdatedEventPayload>): Boolean {
        return EventType.POST_COMMENT_UPDATED == event.type
    }

    override fun findPostId(event: Event<CommentUpdatedEventPayload>): Long? {
        return event.payload?.postId
    }
}