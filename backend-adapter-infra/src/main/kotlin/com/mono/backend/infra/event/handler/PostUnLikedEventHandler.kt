package com.mono.backend.infra.event.handler

import com.mono.backend.common.util.TimeCalculatorUtils
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostUnlikedEventPayload
import com.mono.backend.port.infra.hotpost.cache.HotPostLikeCountCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import org.springframework.stereotype.Component

@Component
class PostUnLikedEventHandler(
    private val hotPostLikeCountCachePort: HotPostLikeCountCachePort,
    private val postQueryModelCache: PostQueryModelCachePort
) : EventHandler<PostUnlikedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostUnlikedEventPayload>) {
        event.payload?.let {
            hotPostLikeCountCachePort.createOrUpdate(
                postId = it.postId,
                likeCount = it.postLikeCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handlePostRead(event: Event<PostUnlikedEventPayload>) {
        event.payload?.let { payload ->
            postQueryModelCache.read(postId = payload.postId)
                ?.let { postQueryModel ->
                    postQueryModel.postLikeCount--
                    postQueryModelCache.update(postQueryModel)
                }
        }
    }

    override fun supports(event: Event<PostUnlikedEventPayload>): Boolean {
        return EventType.POST_UNLIKED == event.type
    }

    override fun findPostId(event: Event<PostUnlikedEventPayload>): Long? {
        return event.payload?.postId
    }
}