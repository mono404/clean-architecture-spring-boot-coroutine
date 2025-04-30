package com.mono.backend.event.hotarticle

import com.mono.backend.cache.hotarticle.HotArticleCommentCountCachePort
import com.mono.backend.cache.hotarticle.HotArticleLikeCountCachePort
import com.mono.backend.cache.hotarticle.HotArticleViewCountCachePort
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class HotArticleScoreCalculator(
    private val hotArticleLikeCountCachePort: HotArticleLikeCountCachePort,
    private val hotArticleViewCountCachePort: HotArticleViewCountCachePort,
    private val hotArticleCommentCountCachePort: HotArticleCommentCountCachePort
) {
    companion object {
        private const val ARTICLE_LIKE_COUNT_WEIGHT = 3
        private const val ARTICLE_COMMENT_COUNT_WEIGHT = 2
        private const val ARTICLE_VIEW_COUNT_WEIGHT = 1

    }

    suspend fun calculate(articleId: Long?): Double = coroutineScope {
        val articleLikeCount = async { hotArticleLikeCountCachePort.read(articleId) }
        val articleViewCount = async { hotArticleViewCountCachePort.read(articleId) }
        val articleCommentCount = async { hotArticleCommentCountCachePort.read(articleId) }

        (articleLikeCount.await() * ARTICLE_LIKE_COUNT_WEIGHT
                + articleViewCount.await() * ARTICLE_VIEW_COUNT_WEIGHT
                + articleCommentCount.await() * ARTICLE_COMMENT_COUNT_WEIGHT).toDouble()
    }
}