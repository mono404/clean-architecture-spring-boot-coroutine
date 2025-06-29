package com.mono.backend.infra.event

import com.mono.backend.common.log.logger
import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PostEventDispatcherPortAdapter(
    private val postEventHandler: PostEventHandler
) : PostEventDispatcherPort {
    private val log = logger()

    override suspend fun dispatch(type: EventType, payload: EventPayload) {
        log.info("[DefaultOutboxEventDispatcher.dispatch] payload=$payload")
        val event = Event(Snowflake.nextId(), type, payload)

        CoroutineScope(Dispatchers.IO).launch {
            postEventHandler.handleEvent(event)
        }
    }
}