package com.mono.backend.event.article.handler

import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload

interface EventHandler<T : EventPayload> {
    suspend fun handleHotArticle(event: Event<T>)
    suspend fun handleArticleRead(event: Event<T>)
    suspend fun supports(event: Event<T>): Boolean
    suspend fun findArticleId(event: Event<T>): Long?
}