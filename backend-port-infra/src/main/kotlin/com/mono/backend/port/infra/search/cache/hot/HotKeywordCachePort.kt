package com.mono.backend.port.infra.search.cache.hot

import com.mono.backend.domain.search.hot.HotKeywordScope

interface HotKeywordCachePort {
    suspend fun increaseKeywordScore(type: String, keyword: String, scope: HotKeywordScope)
    suspend fun getTopKeywords(type: String, scope: HotKeywordScope): List<String>
}