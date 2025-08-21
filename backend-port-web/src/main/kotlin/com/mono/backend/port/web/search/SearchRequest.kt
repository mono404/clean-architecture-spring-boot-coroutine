package com.mono.backend.port.web.search

import com.mono.backend.domain.search.SearchSortOption

data class SearchRequest(
    val keyword: String,
    val sort: SearchSortOption = SearchSortOption.LATEST,
    val lastSearchIndexId: Long? = null,
    val page: Int? = null,
    val size: Int? = null
)
