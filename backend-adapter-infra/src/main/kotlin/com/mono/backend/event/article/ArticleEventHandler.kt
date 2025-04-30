package com.mono.backend.event.article

import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload
import com.mono.backend.event.EventType
import com.mono.backend.event.article.handler.EventHandler
import com.mono.backend.event.hotarticle.HotArticleScoreUpdater
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class ArticleEventHandler(
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater
) {
    suspend fun handleEvent(event: Event<EventPayload>) = coroutineScope {
        eventHandlers.firstOrNull { it.supports(event) }?.let { handler ->
            launch { handler.handleArticleRead(event) }
            launch {
                if (isArticleCreatedOrDeleted(event)) {
                    handler.handleHotArticle(event)
                } else {
                    hotArticleScoreUpdater.update(event, handler)
                }
            }
        }
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return event.type == EventType.ARTICLE_CREATED || event.type == EventType.ARTICLE_DELETED
    }
}