package com.mono.backend.event.post.handler

import com.mono.backend.cache.hotpost.HotPostCommentCountCachePort
import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.CommentDeletedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val hotPostCommentCountCachePort: HotPostCommentCountCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort
) : EventHandler<CommentDeletedEventPayload> {
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
            postQueryModelCachePort.read(postId = payload.postId)
                ?.let { postQueryModel ->
                    postQueryModel.updateBy(payload)
                    postQueryModelCachePort.update(postQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.type
    }

    override suspend fun findPostId(event: Event<CommentDeletedEventPayload>): Long? {
        return event.payload?.postId
    }
}