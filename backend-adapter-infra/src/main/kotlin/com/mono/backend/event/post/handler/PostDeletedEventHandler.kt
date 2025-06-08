package com.mono.backend.event.post.handler

import com.mono.backend.cache.hotpost.HotPostCreatedTimeCachePort
import com.mono.backend.cache.hotpost.HotPostListCachePort
import com.mono.backend.cache.post.BoardPostCountCachePort
import com.mono.backend.cache.post.PostIdListCachePort
import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class PostDeletedEventHandler(
    private val hotPostListCachePort: HotPostListCachePort,
    private val hotPostCreatedTimeCachePort: HotPostCreatedTimeCachePort,

    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
) : EventHandler<PostDeletedEventPayload> {
    override suspend fun handleHotPost(event: Event<PostDeletedEventPayload>) {
        event.payload?.let {
            hotPostCreatedTimeCachePort.delete(it.postId)
            hotPostListCachePort.remove(it.postId, it.createdAt)
        }
    }

    override suspend fun handlePostRead(event: Event<PostDeletedEventPayload>) {
        event.payload?.let { payload ->
            // 순서 중요 항상 조회의 키값인 ID 먼저 제거해준다.
            postIdListCachePort.delete(payload.boardId, payload.postId)
            postQueryModelCachePort.delete(payload.postId)
            boardPostCountCachePort.createOrUpdate(payload.boardId, payload.boardPostCount)
        }
    }

    override suspend fun supports(event: Event<PostDeletedEventPayload>): Boolean {
        return EventType.POST_DELETED == event.type
    }

    override suspend fun findPostId(event: Event<PostDeletedEventPayload>): Long? {
        return event.payload?.postId
    }
}