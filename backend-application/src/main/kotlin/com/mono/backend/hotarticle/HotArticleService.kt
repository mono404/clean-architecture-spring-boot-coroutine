package com.mono.backend.hotarticle

import com.mono.backend.article.ArticleQueryService
import com.mono.backend.cache.hotarticle.HotArticleListCachePort
import com.mono.backend.hotarticle.response.HotArticleResponse
import org.springframework.stereotype.Service

@Service
class HotArticleService(
    private val articleQueryService: ArticleQueryService,
    private val hotArticleListCachePort: HotArticleListCachePort
) : HotArticleUseCase {
    override suspend fun readAll(dateStr: String): List<HotArticleResponse>? {
        return hotArticleListCachePort.readAll(dateStr)
            ?.mapNotNull { articleQueryService.read(it!!) }
            ?.map(HotArticleResponse::from)
    }
}