package com.mono.backend.infra.persistence.post.view

import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostViewedEventPayload
import com.mono.backend.domain.post.view.PostViewCount
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.event.EventDispatcherPort
import com.mono.backend.port.infra.view.persistence.PostViewCountBackUpProcessorPort
import org.springframework.stereotype.Component

@Component
class PostViewCountBackUpProcessorAdapter(
    private val postViewCountBackUpPersistenceAdapter: PostViewCountBackupPersistenceAdapter,
    private val eventDispatcherPort: EventDispatcherPort
) : PostViewCountBackUpProcessorPort {
    override suspend fun backup(postId: Long, viewCount: Long) {
        transaction {
            val result = postViewCountBackUpPersistenceAdapter.updateViewCount(postId, viewCount)
            if (result == 0) {
                val postViewCount = postViewCountBackUpPersistenceAdapter.findById(postId)
                if (postViewCount == null) {
                    postViewCountBackUpPersistenceAdapter.save(PostViewCount(postId, viewCount))

                    eventDispatcherPort.dispatch(
                        type = EventType.POST_VIEWED,
                        payload = PostViewedEventPayload(
                            postId = postId,
                            postViewCount = viewCount
                        )
                    )
                }
            }
        }
    }
}