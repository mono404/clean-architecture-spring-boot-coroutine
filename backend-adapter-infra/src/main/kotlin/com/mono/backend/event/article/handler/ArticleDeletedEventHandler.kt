package com.mono.backend.event.article.handler

import com.mono.backend.cache.article.ArticleIdListCachePort
import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.article.BoardArticleCountCachePort
import com.mono.backend.cache.hotarticle.HotArticleCreatedTimeCachePort
import com.mono.backend.cache.hotarticle.HotArticleListCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.ArticleDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleListCachePort: HotArticleListCachePort,
    private val hotArticleCreatedTimeCachePort: HotArticleCreatedTimeCachePort,

    private val articleQueryModelCachePort: ArticleQueryModelCachePort,
    private val articleIdListCachePort: ArticleIdListCachePort,
    private val boardArticleCountCachePort: BoardArticleCountCachePort,
) : EventHandler<ArticleDeletedEventPayload> {
    override suspend fun handleHotArticle(event: Event<ArticleDeletedEventPayload>) {
        event.payload?.let {
            hotArticleCreatedTimeCachePort.delete(it.articleId)
            hotArticleListCachePort.remove(it.articleId, it.createdAt)
        }
    }

    override suspend fun handleArticleRead(event: Event<ArticleDeletedEventPayload>) {
        event.payload?.let { payload ->
            // 순서 중요 항상 조회의 키값인 ID 먼저 제거해준다.
            articleIdListCachePort.delete(payload.boardId, payload.articleId)
            articleQueryModelCachePort.delete(payload.articleId)
            boardArticleCountCachePort.createOrUpdate(payload.boardId, payload.boardArticleCount)
        }
    }

    override suspend fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == event.type
    }

    override suspend fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long? {
        return event.payload?.articleId
    }
}