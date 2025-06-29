package com.mono.backend.port.infra.post.event

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType

interface PostEventDispatcherPort {
    suspend fun dispatch(type: EventType, payload: EventPayload)
}