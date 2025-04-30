package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.hotarticle.HotArticleLikeCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleUnlikedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleUnLikedEventHandler(
    private val hotArticleLikeCountCachePort: HotArticleLikeCountCachePort,

    private val articleQueryModelCache: ArticleQueryModelCachePort
) : EventHandler<ArticleUnlikedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleUnlikedEventPayload>) {
        event.payload?.let {
            hotArticleLikeCountCachePort.createOrUpdate(
                articleId = it.articleId,
                likeCount = it.articleLikeCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<ArticleUnlikedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCache.read(articleId = payload.articleId)
                ?.let { articleQueryModel ->
                    articleQueryModel.updateBy(payload)
                    articleQueryModelCache.update(articleQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleUnlikedEventPayload>): Long? {
        return event.payload?.articleId
    }
}