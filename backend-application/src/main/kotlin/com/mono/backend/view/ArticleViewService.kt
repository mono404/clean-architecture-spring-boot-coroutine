package com.mono.backend.view

import com.mono.backend.cache.view.ArticleViewCountCachePort
import com.mono.backend.cache.view.ArticleViewDistributedLockCachePort
import com.mono.backend.persistence.view.ArticleViewCountBackUpProcessorPort
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    private val articleViewCountCachePort: ArticleViewCountCachePort,
    private val articleViewCountBackUpProcessorPort: ArticleViewCountBackUpProcessorPort,
    private val articleViewDistributedLockCachePort: ArticleViewDistributedLockCachePort
) : ArticleViewUseCase {
    companion object {
        const val BACK_UP_BATCH_SIZE = 100
        val TTL: Duration = Duration.ofMinutes(10)
    }

    override suspend fun increase(articleId: Long, userId: Long): Long? {
        if (articleViewDistributedLockCachePort.lock(articleId, userId, TTL) != true) {
            return articleViewCountCachePort.read(articleId)
        }

        val count = articleViewCountCachePort.increase(articleId)
        if (count != null) {
            if (count % BACK_UP_BATCH_SIZE == 0L) {
                articleViewCountBackUpProcessorPort.backup(articleId, count)
            }
        }
        return count
    }

    override suspend fun count(articleId: Long): Long {
        return articleViewCountCachePort.read(articleId)
    }
}