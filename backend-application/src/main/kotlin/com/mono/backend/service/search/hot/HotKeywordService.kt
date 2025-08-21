package com.mono.backend.service.search.hot

import com.mono.backend.domain.search.hot.HotKeywordScope
import com.mono.backend.port.infra.search.cache.hot.HotKeywordCachePort
import com.mono.backend.port.web.search.hot.HotKeywordUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class HotKeywordService(
    private val hotKeywordCachePort: HotKeywordCachePort
) : HotKeywordUseCase {
    override suspend fun recordKeyword(type: String, keyword: String) {
        if (keyword.isBlank() || keyword.length < 2) return

        coroutineScope {
            launch {
                HotKeywordScope.entries.forEach { scope ->
                    hotKeywordCachePort.increaseKeywordScore(type, keyword, scope)
                }
            }
        }
    }

    override suspend fun getHotKeywords(type: String, scope: HotKeywordScope): List<String> {
        return hotKeywordCachePort.getTopKeywords(type, scope)
    }
}