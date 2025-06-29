package com.mono.backend.port.web.search

import com.mono.backend.domain.search.SearchIndex
import com.mono.backend.domain.search.SearchSortOption

interface SearchQueryUseCase {
    suspend fun search(
        keyword: String,
        sort: SearchSortOption,
        lastSearchIndexId: Long?,
        page: Int,
        size: Int
    ): List<SearchIndex>
}