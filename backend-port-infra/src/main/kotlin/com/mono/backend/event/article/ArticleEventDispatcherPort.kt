package com.mono.backend.event.article

import com.mono.backend.event.EventPayload
import com.mono.backend.event.EventType

interface ArticleEventDispatcherPort {
    suspend fun dispatch(type: EventType, payload: EventPayload)
}