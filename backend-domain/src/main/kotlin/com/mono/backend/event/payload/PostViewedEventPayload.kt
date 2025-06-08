package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload

data class PostViewedEventPayload(
    val postId: Long = 0,
    val postViewCount: Long = 0
): EventPayload {

}
