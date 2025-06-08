package com.mono.backend.event.post.handler

import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostUpdatedEventPayload
import org.springframework.stereotype.Component

@Component
class PostUpdatedEventHandler(
    private val postQueryModelCachePort: PostQueryModelCachePort
) : EventHandler<PostUpdatedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostUpdatedEventPayload>) {
        return
    }

    override suspend fun handlePostRead(event: Event<PostUpdatedEventPayload>) {
        event.payload?.let { payload ->
            postQueryModelCachePort.read(postId = payload.postId)
                ?.let { postQueryModel ->
                    postQueryModel.updateBy(payload)
                    postQueryModelCachePort.update(postQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<PostUpdatedEventPayload>): Boolean {
        return EventType.POST_UPDATED == event.type
    }

    override suspend fun findPostId(event: Event<PostUpdatedEventPayload>): Long? {
        return event.payload?.postId
    }
}