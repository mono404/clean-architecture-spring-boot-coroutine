package com.mono.backend.event.post

import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload
import com.mono.backend.event.EventType
import com.mono.backend.event.hotpost.HotPostScoreUpdater
import com.mono.backend.event.post.handler.EventHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PostEventHandler(
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotPostScoreUpdater: HotPostScoreUpdater
) {
    suspend fun handleEvent(event: Event<EventPayload>) = coroutineScope {
        eventHandlers.firstOrNull { it.supports(event) }?.let { handler ->
            launch { handler.handlePostRead(event) }
            launch {
                if (isPostCreatedOrDeleted(event)) {
                    handler.handleHotPost(event)
                } else {
                    hotPostScoreUpdater.update(event, handler)
                }
            }
        }
    }

    private fun isPostCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return event.type == EventType.POST_CREATED || event.type == EventType.POST_DELETED
    }
}