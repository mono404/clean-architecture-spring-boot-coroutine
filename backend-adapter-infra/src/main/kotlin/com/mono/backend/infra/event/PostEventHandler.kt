package com.mono.backend.infra.event

import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.infra.event.handler.EventHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PostEventHandler(
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotPostScoreUpdater: HotPostScoreUpdater
) {
    suspend fun handleEvent(event: Event<EventPayload>) = coroutineScope {
        eventHandlers.filter { it.supports(event) }.forEach { handler ->
            /** 게시글 캐시 업데이트 이벤트 */
            launch { handler.handlePostRead(event) }
            /** 인기 게시글 업데이트 이벤트 */
            launch {
                if (isPostCreatedOrDeleted(event)) {
                    handler.handleHotPost(event)
                } else {
                    hotPostScoreUpdater.update(event, handler)
                }
            }
            /** Full Text Search 용 테이블 업데이트 */
            launch { handler.handleSearchIndex(event) }
        }
    }

    private fun isPostCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return event.type == EventType.POST_CREATED || event.type == EventType.POST_DELETED
    }
}