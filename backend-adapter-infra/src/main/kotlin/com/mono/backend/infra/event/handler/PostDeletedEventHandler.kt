package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostDeletedEventPayload
import com.mono.backend.port.infra.hotpost.cache.HotPostCreatedTimeCachePort
import com.mono.backend.port.infra.hotpost.cache.HotPostListCachePort
import com.mono.backend.port.infra.post.cache.BoardPostCountCachePort
import com.mono.backend.port.infra.post.cache.PostIdListCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Component

@Component
class PostDeletedEventHandler(
    private val hotPostListCachePort: HotPostListCachePort,
    private val hotPostCreatedTimeCachePort: HotPostCreatedTimeCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
    private val searchPersistencePort: SearchPersistencePort
) : EventHandler<PostDeletedEventPayload> {
    private val log = logger()

    override suspend fun handleHotPost(event: Event<PostDeletedEventPayload>) {
        event.payload?.let {
            hotPostCreatedTimeCachePort.delete(it.postId)
            hotPostListCachePort.remove(it.postId, it.createdAt)
        }
    }

    override suspend fun handlePostRead(event: Event<PostDeletedEventPayload>) {
        event.payload?.let { payload ->
            // 순서 중요 항상 조회의 키값인 ID 먼저 제거해준다.
            postIdListCachePort.delete(payload.boardType, payload.postId)
            postQueryModelCachePort.delete(payload.postId)
            boardPostCountCachePort.createOrUpdate(payload.boardType, payload.boardPostCount)
        }
    }

    override suspend fun handleSearchIndex(event: Event<PostDeletedEventPayload>) {
        event.payload?.let { payload ->
            try {
                searchPersistencePort.deleteById(payload.postId)
            } catch (e: Exception) {
                log.error("Failed to delete search index for post ${payload.postId}", e)
            }
        }
    }

    override suspend fun supports(event: Event<PostDeletedEventPayload>): Boolean {
        return EventType.POST_DELETED == event.type
    }

    override suspend fun findPostId(event: Event<PostDeletedEventPayload>): Long? {
        return event.payload?.postId
    }
}