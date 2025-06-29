package com.mono.backend.infra.persistence.searchindex

import com.mono.backend.domain.search.SearchIndex
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import org.springframework.stereotype.Repository

@Repository
class SearchIndexPersistenceAdapter(
    private val searchIndexRepository: SearchIndexRepository
) : SearchPersistencePort {
    override suspend fun save(searchIndex: SearchIndex): SearchIndex {
        return searchIndexRepository.save(SearchIndexEntity.from(searchIndex)).toDomain()
    }

    override suspend fun searchByLatest(keyword: String, lastSearchIndexId: Long?, limit: Int): List<SearchIndex> {
        val cursorId = lastSearchIndexId ?: Long.MAX_VALUE
        return searchIndexRepository.searchByLatest(keyword, cursorId, limit).map { it.toDomain() }
    }

    override suspend fun searchByRelevance(keyword: String, page: Int, size: Int): List<SearchIndex> {
        val offset = page * size
        return searchIndexRepository.searchByRelevance(keyword, offset, size).map { it.toDomain() }
    }

    override suspend fun deleteById(searchIndexId: Long) {
        searchIndexRepository.deleteById(searchIndexId)
    }

    override suspend fun findById(searchIndexId: Long): SearchIndex? {
        return searchIndexRepository.findById(searchIndexId)?.toDomain()
    }
}