package com.mono.backend.port.infra.event

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType

interface EventDispatcherPort {
    suspend fun dispatch(type: EventType, payload: EventPayload)
}