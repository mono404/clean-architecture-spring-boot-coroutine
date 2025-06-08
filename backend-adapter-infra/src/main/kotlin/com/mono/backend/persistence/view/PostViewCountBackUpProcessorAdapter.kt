package com.mono.backend.persistence.view

import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostViewedEventPayload
import com.mono.backend.event.post.PostEventDispatcherPort
import com.mono.backend.transaction.transaction
import com.mono.backend.view.PostViewCount
import org.springframework.stereotype.Component

@Component
class PostViewCountBackUpProcessorAdapter(
    private val postViewCountBackUpPersistenceAdapter: PostViewCountBackupPersistenceAdapter,
    private val postEventDispatcherPort: PostEventDispatcherPort
) : PostViewCountBackUpProcessorPort {
    override suspend fun backup(postId: Long, viewCount: Long) {
        transaction {
            val result = postViewCountBackUpPersistenceAdapter.updateViewCount(postId, viewCount)
            if (result == 0) {
                val postViewCount = postViewCountBackUpPersistenceAdapter.findById(postId)
                if (postViewCount == null) {
                    postViewCountBackUpPersistenceAdapter.save(PostViewCount(postId, viewCount))

                    postEventDispatcherPort.dispatch(
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