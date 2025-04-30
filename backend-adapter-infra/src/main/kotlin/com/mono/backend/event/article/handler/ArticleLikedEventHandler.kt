package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.hotarticle.HotArticleLikeCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleLikedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val hotArticleLikeCountCachePort: HotArticleLikeCountCachePort,

    private val articleQueryModelCachePort: ArticleQueryModelCachePort
) : EventHandler<ArticleLikedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleLikedEventPayload>) {
        event.payload?.let {
            hotArticleLikeCountCachePort.createOrUpdate(
                articleId = it.articleId,
                likeCount = it.articleLikeCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<ArticleLikedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCachePort.read(payload.articleId)
                ?.let { articleQueryModel ->
                    articleQueryModel.updateBy(payload)
                    articleQueryModelCachePort.update(articleQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return EventType.ARTICLE_LIKED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleLikedEventPayload>): Long? {
        return event.payload?.articleId
    }
}