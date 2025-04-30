package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.hotarticle.HotArticleCommentCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.CommentCreatedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val hotArticleCommentCountCachePort: HotArticleCommentCountCachePort,

    private val articleQueryModelCachePort: ArticleQueryModelCachePort
) : EventHandler<CommentCreatedEventPayload> {
    override suspend fun handleHotArticle(event: Event<CommentCreatedEventPayload>) {
        event.payload?.let {
            hotArticleCommentCountCachePort.createOrUpdate(
                articleId = it.articleId,
                commentCount = it.articleCommentCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<CommentCreatedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCachePort.read(articleId = payload.articleId)
                ?.let { articleQueryModel ->
                    articleQueryModel.updateBy(payload)
                    articleQueryModelCachePort.update(articleQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return EventType.COMMENT_CREATED == event.type
    }

    override suspend fun findArticleId(event: Event<CommentCreatedEventPayload>): Long? {
        return event.payload?.articleId
    }
}