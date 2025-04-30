package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.hotarticle.HotArticleCommentCountCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.CommentDeletedEventPayload
import com.mono.backend.util.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val hotArticleCommentCountCachePort: HotArticleCommentCountCachePort,

    private val articleQueryModelCachePort: ArticleQueryModelCachePort
) : EventHandler<CommentDeletedEventPayload> {
    override suspend fun handleHotArticle(event: Event<CommentDeletedEventPayload>) {
        event.payload?.let {
            hotArticleCommentCountCachePort.createOrUpdate(
                articleId = it.articleId,
                commentCount = it.articleCommentCount,
                ttl = TimeCalculatorUtils.calculateDurationToMidnight()
            )
        }
    }

    override suspend fun handleArticleRead(event: Event<CommentDeletedEventPayload>) {
        event.payload?.let { payload ->
            articleQueryModelCachePort.read(articleId = payload.articleId)
                ?.let { articleQueryModel ->
                    articleQueryModel.updateBy(payload)
                    articleQueryModelCachePort.update(articleQueryModel)
                }
        }
    }

    override suspend fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.type
    }

    override suspend fun findArticleId(event: Event<CommentDeletedEventPayload>): Long? {
        return event.payload?.articleId
    }
}