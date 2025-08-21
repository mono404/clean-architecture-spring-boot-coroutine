package com.mono.backend.service.search

import com.mono.backend.domain.search.SearchIndex
import com.mono.backend.domain.search.SearchSortOption
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import com.mono.backend.port.web.search.SearchQueryUseCase
import org.springframework.stereotype.Service

@Service
class SearchQueryService(
    private val searchPersistencePort: SearchPersistencePort,
) : SearchQueryUseCase {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    override suspend fun search(
        keyword: String,
        sort: SearchSortOption,
        lastSearchIndexId: Long?,
        page: Int,
        size: Int
    ): List<SearchIndex> {
        return when (sort) {
            SearchSortOption.LATEST -> {
                searchPersistencePort.searchByLatest(keyword, lastSearchIndexId, DEFAULT_PAGE_SIZE)
            }

            SearchSortOption.RELEVANCE -> {
                searchPersistencePort.searchByRelevance(keyword, page, size)
            }
        }
    }
}