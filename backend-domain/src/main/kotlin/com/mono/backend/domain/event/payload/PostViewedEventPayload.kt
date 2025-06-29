package com.mono.backend.domain.event.payload

import com.mono.backend.domain.event.EventPayload

data class PostViewedEventPayload(
    val postId: Long = 0,
    val postViewCount: Long = 0
) : EventPayload
