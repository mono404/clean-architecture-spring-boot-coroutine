package com.mono.backend.event.post

import com.mono.backend.event.EventPayload
import com.mono.backend.event.EventType

interface PostEventDispatcherPort {
    suspend fun dispatch(type: EventType, payload: EventPayload)
}