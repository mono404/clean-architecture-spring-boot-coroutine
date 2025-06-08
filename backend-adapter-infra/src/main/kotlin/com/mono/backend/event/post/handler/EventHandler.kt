package com.mono.backend.event.post.handler

import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload

interface EventHandler<T : EventPayload> {
    suspend fun handleHotPost(event: Event<T>)
    suspend fun handlePostRead(event: Event<T>)
    suspend fun supports(event: Event<T>): Boolean
    suspend fun findPostId(event: Event<T>): Long?
}