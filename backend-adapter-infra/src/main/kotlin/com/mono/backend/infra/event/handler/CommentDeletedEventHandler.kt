package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.CommentDeletedEventPayload
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePortV2
import com.mono.backend.port.infra.hotpost.cache.HotPostCommentCountCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val hotPostCommentCountCachePort: HotPostCommentCountCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val commentPersistencePortV2: CommentPersistencePortV2,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<CommentDeletedEventPayload> {
    private val log = logger()
    override suspend fun handleHotPost(event: Event<CommentDeletedEventPayload>) {
        event.payload?.let {
            hotPostCommentCountCachePort.createOrUpdate(
                postId = it.postId,
                commentCount = it.postCommentCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handlePostRead(event: Event<CommentDeletedEventPayload>) {
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

    /** Full Re-Aggregation 방식 */
    override suspend fun handleSearchIndex(event: Event<CommentDeletedEventPayload>) {
        event.payload?.let { payload ->
            val allComments = commentPersistencePortV2.findAllByPostId(payload.postId)

            val combinedText = allComments.filterNot { it.commentId == payload.commentId }
                .joinToString(" ") { it.content }

            val existing = searchPersistencePort.findById(payload.postId) ?: return
            val updated = existing.copy(comment = combinedText)

            searchPersistencePort.save(updated)
        }
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.POST_COMMENT_DELETED == event.type
    }

    override fun findPostId(event: Event<CommentDeletedEventPayload>): Long? {
        return event.payload?.postId
    }
}