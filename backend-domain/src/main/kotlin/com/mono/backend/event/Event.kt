package com.mono.backend.event

data class Event<T : EventPayload>(
    val eventId: Long? = null,
    val type: EventType? = null,
    val payload: T? = null
)