package com.mono.backend.port.web.search

import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.domain.search.DomainType
import com.mono.backend.domain.search.SearchIndex
import java.time.LocalDateTime

data class SearchResponse(
    val searchIndexId: Long,
    val domainType: DomainType,
    val boardType: BoardType,
    val title: String?,
    val content: String?,
    val comment: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(searchIndex: SearchIndex) = SearchResponse(
            searchIndexId = searchIndex.searchIndexId,
            domainType = searchIndex.domainType,
            boardType = searchIndex.boardType,
            title = searchIndex.title,
            content = searchIndex.content,
            comment = searchIndex.comment,
            createdAt = searchIndex.createdAt!!
        )
    }
}