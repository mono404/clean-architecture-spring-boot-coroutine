package com.mono.backend.persistence.view

import com.mono.backend.event.EventType
import com.mono.backend.event.article.ArticleEventDispatcherPort
import com.mono.backend.event.payload.ArticleViewedEventPayload
import com.mono.backend.transaction.transaction
import com.mono.backend.view.ArticleViewCount
import org.springframework.stereotype.Component

@Component
class ArticleViewCountBackUpProcessorAdapter(
    private val articleViewCountBackUpPersistenceAdapter: ArticleViewCountBackupPersistenceAdapter,
    private val articleEventDispatcherPort: ArticleEventDispatcherPort
) : ArticleViewCountBackUpProcessorPort {
    override suspend fun backup(articleId: Long, viewCount: Long) {
        transaction {
            val result = articleViewCountBackUpPersistenceAdapter.updateViewCount(articleId, viewCount)
            if (result == 0) {
                val articleViewCount = articleViewCountBackUpPersistenceAdapter.findById(articleId)
                if (articleViewCount == null) {
                    articleViewCountBackUpPersistenceAdapter.save(ArticleViewCount(articleId, viewCount))

                    articleEventDispatcherPort.dispatch(
                        type = EventType.ARTICLE_VIEWED,
                        payload = ArticleViewedEventPayload(
                            articleId = articleId,
                            articleViewCount = viewCount
                        )
                    )
                }
            }
        }
    }
}