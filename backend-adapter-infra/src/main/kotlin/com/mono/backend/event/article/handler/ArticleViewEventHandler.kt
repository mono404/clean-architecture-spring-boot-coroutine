package com.mono.backend.event.article.handler

import com.mono.backend.cache.hotarticle.HotArticleViewCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleViewedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleViewEventHandler(
    private val hotArticleViewCountCachePort: HotArticleViewCountCachePort
) : EventHandler<ArticleViewedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleViewedEventPayload>) {
        event.payload?.let {
            hotArticleViewCountCachePort.createOrUpdate(
                articleId = it.articleId,
                viewCount = it.articleViewCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<ArticleViewedEventPayload>) {
        return
    }

    override suspend fun supports(event: Event<ArticleViewedEventPayload>): Boolean {
        return EventType.ARTICLE_VIEWED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleViewedEventPayload>): Long? {
        return event.payload?.articleId
    }
}