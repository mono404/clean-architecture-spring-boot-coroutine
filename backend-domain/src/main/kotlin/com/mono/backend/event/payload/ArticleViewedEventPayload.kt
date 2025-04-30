package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload

data class ArticleViewedEventPayload(
    val articleId: Long = 0,
    val articleViewCount: Long = 0
): EventPayload {

}
