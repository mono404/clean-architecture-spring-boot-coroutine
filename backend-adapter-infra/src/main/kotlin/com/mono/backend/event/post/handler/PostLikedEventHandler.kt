package com.mono.backend.event.post.handler

import com.mono.backend.cache.hotpost.HotPostLikeCountCachePort
import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostLikedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class PostLikedEventHandler(
    private val hotPostLikeCountCachePort: HotPostLikeCountCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort
) : EventHandler<PostLikedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostLikedEventPayload>) {
        event.payload?.let {
            hotPostLikeCountCachePort.createOrUpdate(
                postId = it.postId,
                likeCount = it.postLikeCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handlePostRead(event: Event<PostLikedEventPayload>) {
        event.payload?.let { payload ->
            postQueryModelCachePort.read(payload.postId)
                ?.let { postQueryModel ->
                    postQueryModel.updateBy(payload)
                    postQueryModelCachePort.update(postQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<PostLikedEventPayload>): Boolean {
        return EventType.POST_LIKED == event.type
    }

    override suspend fun findPostId(event: Event<PostLikedEventPayload>): Long? {
        return event.payload?.postId
    }
}