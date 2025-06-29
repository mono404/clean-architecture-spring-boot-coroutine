package com.mono.backend.service.post.hot

import com.mono.backend.port.infra.hotpost.cache.HotPostListCachePort
import com.mono.backend.port.web.post.hot.HotPostUseCase
import com.mono.backend.port.web.post.hot.dto.HotPostResponse
import com.mono.backend.service.post.PostQueryService
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