package com.mono.backend.view

import com.mono.backend.cache.view.PostViewCountCachePort
import com.mono.backend.cache.view.PostViewDistributedLockCachePort
import com.mono.backend.persistence.view.PostViewCountBackUpProcessorPort
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PostViewService(
    private val postViewCountCachePort: PostViewCountCachePort,
    private val postViewCountBackUpProcessorPort: PostViewCountBackUpProcessorPort,
    private val postViewDistributedLockCachePort: PostViewDistributedLockCachePort
) : PostViewUseCase {
    companion object {
        const val BACK_UP_BATCH_SIZE = 100
        val TTL: Duration = Duration.ofMinutes(10)
    }

    override suspend fun increase(postId: Long, memberId: Long): Long? {
        if (postViewDistributedLockCachePort.lock(postId, memberId, TTL) != true) {
            return postViewCountCachePort.read(postId)
        }

        val count = postViewCountCachePort.increase(postId)
        if (count != null) {
            if (count % BACK_UP_BATCH_SIZE == 0L) {
                postViewCountBackUpProcessorPort.backup(postId, count)
            }
        }
        return count
    }

    override suspend fun count(postId: Long): Long {
        return postViewCountCachePort.read(postId)
    }
}