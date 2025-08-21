package com.mono.backend.port.web.search.hot

import com.mono.backend.domain.search.hot.HotKeywordScope

interface HotKeywordUseCase {
    suspend fun recordKeyword(type: String, keyword: String)
    suspend fun getHotKeywords(type: String, scope: HotKeywordScope): List<String>
}