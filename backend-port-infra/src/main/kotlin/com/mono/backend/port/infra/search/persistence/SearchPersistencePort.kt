package com.mono.backend.port.infra.search.persistence

import com.mono.backend.domain.search.SearchIndex

interface SearchPersistencePort {
    suspend fun save(searchIndex: SearchIndex): SearchIndex
    suspend fun searchByLatest(keyword: String, lastSearchIndexId: Long?, limit: Int): List<SearchIndex>
    suspend fun searchByRelevance(keyword: String, page: Int, size: Int): List<SearchIndex>
    suspend fun deleteById(searchIndexId: Long)
    suspend fun findById(searchIndexId: Long): SearchIndex?
}