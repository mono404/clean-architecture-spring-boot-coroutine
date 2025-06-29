package com.mono.backend.infra.persistence.searchindex

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SearchIndexRepository : CoroutineCrudRepository<SearchIndexEntity, Long> {
    @Query(
        """
        SELECT * FROM search_index
        WHERE MATCH(title, content, comment) AGAINST (:keyword IN BOOLEAN MODE)
        AND searchIndexId < :lastSearchIndexId
        ORDER BY searchIndexId DESC;
        LIMIT :limit;
    """
    )
    suspend fun searchByLatest(keyword: String, lastSearchIndexId: Long, limit: Int): List<SearchIndexEntity>

    @Query(
        """
        SELECT *, MATCH(title, content, comment) AGAINST (:keyword IN BOOLEAN MODE) AS score
        FROM search_index
        WHERE MATCH(title, content, comment) AGAINST (:keyword IN BOOLEAN MODE)
        ORDER BY score DESC
        LIMIT :limit OFFSET :offset;
    """
    )
    suspend fun searchByRelevance(keyword: String, offset: Int, limit: Int): List<SearchIndexEntity>
}