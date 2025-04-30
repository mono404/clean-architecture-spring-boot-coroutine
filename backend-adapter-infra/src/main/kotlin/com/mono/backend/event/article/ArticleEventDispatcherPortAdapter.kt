package com.mono.backend.event.article

import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload
import com.mono.backend.event.EventType
import com.mono.backend.log.logger
import com.mono.backend.snowflake.Snowflake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class ArticleEventDispatcherPortAdapter(
    private val articleEventHandler: ArticleEventHandler
) : ArticleEventDispatcherPort {
    private val log = logger()

    override suspend fun dispatch(type: EventType, payload: EventPayload) {
        log.info("[DefaultOutboxEventDispatcher.dispatch] payload=$payload")
        val event = Event(Snowflake.nextId(), type, payload)

        CoroutineScope(Dispatchers.IO).launch {
            articleEventHandler.handleEvent(event)
        }
    }
}