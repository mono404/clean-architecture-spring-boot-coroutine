package com.mono.backend.hotpost

import com.mono.backend.cache.hotpost.HotPostListCachePort
import com.mono.backend.hotpost.response.HotPostResponse
import com.mono.backend.post.PostQueryService
import org.springframework.stereotype.Service

@Service
class HotPostService(
    private val postQueryService: PostQueryService,
    private val hotPostListCachePort: HotPostListCachePort
) : HotPostUseCase {
    override suspend fun readAll(dateStr: String): List<HotPostResponse>? {
        return hotPostListCachePort.readAll(dateStr)
            ?.mapNotNull { postQueryService.read(it!!) }
            ?.map(HotPostResponse::from)
    }
}