package com.mono.backend.infra.event

import com.mono.backend.port.infra.hotpost.cache.HotPostCommentCountCachePort
import com.mono.backend.port.infra.hotpost.cache.HotPostLikeCountCachePort
import com.mono.backend.port.infra.hotpost.cache.HotPostViewCountCachePort
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class HotPostScoreCalculator(
    private val hotPostLikeCountCachePort: HotPostLikeCountCachePort,
    private val hotPostViewCountCachePort: HotPostViewCountCachePort,
    private val hotPostCommentCountCachePort: HotPostCommentCountCachePort
) {
    companion object {
        private const val POST_LIKE_COUNT_WEIGHT = 3
        private const val POST_COMMENT_COUNT_WEIGHT = 2
        private const val POST_VIEW_COUNT_WEIGHT = 1

    }

    suspend fun calculate(postId: Long?): Double = coroutineScope {
        val postLikeCount = async { hotPostLikeCountCachePort.read(postId) }
        val postViewCount = async { hotPostViewCountCachePort.read(postId) }
        val postCommentCount = async { hotPostCommentCountCachePort.read(postId) }

        (postLikeCount.await() * POST_LIKE_COUNT_WEIGHT
                + postViewCount.await() * POST_VIEW_COUNT_WEIGHT
                + postCommentCount.await() * POST_COMMENT_COUNT_WEIGHT).toDouble()
    }
}