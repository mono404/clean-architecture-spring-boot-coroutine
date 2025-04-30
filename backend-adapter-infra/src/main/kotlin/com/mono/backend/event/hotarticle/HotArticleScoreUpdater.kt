package com.mono.backend.event.hotarticle

import com.mono.backend.cache.hotarticle.HotArticleCreatedTimeCachePort
import com.mono.backend.cache.hotarticle.HotArticleListCachePort
import com.mono.backend.event.Event
import com.mono.backend.event.EventPayload
import com.mono.backend.event.article.handler.EventHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleListCachePort: HotArticleListCachePort,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val hotArticleCreatedTimeCachePort: HotArticleCreatedTimeCachePort
) {
    companion object {
        private const val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL: Duration = Duration.ofDays(10)
    }

    suspend fun update(event: Event<EventPayload>, eventHandler: EventHandler<EventPayload>) = coroutineScope {
        val articleId = eventHandler.findArticleId(event)
        val createdTime = hotArticleCreatedTimeCachePort.read(articleId)

        if (!isArticleCreatedToday(createdTime)) return@coroutineScope

        launch { eventHandler.handleHotArticle(event) }

        val score = hotArticleScoreCalculator.calculate(articleId)
        hotArticleListCachePort.add(articleId, createdTime, score, HOT_ARTICLE_COUNT, HOT_ARTICLE_TTL)
    }

    private fun isArticleCreatedToday(createdTime: LocalDateTime?): Boolean {
        return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now())
    }
}