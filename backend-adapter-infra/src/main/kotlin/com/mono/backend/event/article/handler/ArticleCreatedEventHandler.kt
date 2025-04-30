package com.mono.backend.event.article.handler

import com.mono.backend.article.ArticleQueryModel
import com.mono.backend.cache.article.ArticleIdListCachePort
import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.article.BoardArticleCountCachePort
import com.mono.backend.cache.hotarticle.HotArticleCreatedTimeCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleCreatedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val hotArticleCreatedTimeCachePort: HotArticleCreatedTimeCachePort,

    private val articleQueryModelCachePort: ArticleQueryModelCachePort,
    private val articleIdListCachePort: ArticleIdListCachePort,
    private val boardArticleCountCachePort: BoardArticleCountCachePort,
) : EventHandler<ArticleCreatedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleCreatedEventPayload>) {
        event.payload?.let {
            hotArticleCreatedTimeCachePort.createOrUpdate(
                articleId = it.articleId,
                createdAt = it.createdAt,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<ArticleCreatedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCachePort.create(
                ArticleQueryModel.create(payload),
                Duration.ofDays(1)
            )
            articleIdListCachePort.add(payload.boardId, payload.articleId, 1000L)
            boardArticleCountCachePort.createOrUpdate(payload.boardId, payload.boardArticleCount)
        }
    }

    override suspend fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleCreatedEventPayload>): Long? {
        return event.payload?.articleId
    }
}