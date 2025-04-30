package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleUpdatedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUpdatedEventHandler(
    private val articleQueryModelCachePort: ArticleQueryModelCachePort
) : EventHandler<ArticleUpdatedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleUpdatedEventPayload>) {
        return
    }

    override suspend fun handleArticleRead(event: Event<ArticleUpdatedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCachePort.read(articleId = payload.articleId)
                ?.let { articleQueryModel ->
                    articleQueryModel.updateBy(payload)
                    articleQueryModelCachePort.update(articleQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<ArticleUpdatedEventPayload>): Boolean {
        return EventType.ARTICLE_UPDATED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleUpdatedEventPayload>): Long? {
        return event.payload?.articleId
    }
}