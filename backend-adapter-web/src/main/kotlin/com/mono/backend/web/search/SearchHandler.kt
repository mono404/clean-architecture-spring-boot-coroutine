package com.mono.backend.web.search

import com.mono.backend.domain.search.SearchSortOption
import com.mono.backend.domain.search.toSearchSortOption
import com.mono.backend.port.web.search.SearchQueryUseCase
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class SearchHandler(
    private val searchQueryUseCase: SearchQueryUseCase,
) : DefaultHandler {
    suspend fun search(serverRequest: ServerRequest): ServerResponse {
        val keyword = serverRequest.queryParamOrNull("keyword") ?: return badRequest("Missing keyword")

        val sort = serverRequest.queryParamOrNull("sort").toSearchSortOption() ?: SearchSortOption.LATEST

        val lastSearchIndexId = serverRequest.queryParamOrNull("lastSearchIndexId")?.toLongOrNull()
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull() ?: 0
        val size = serverRequest.queryParamOrNull("size")?.toIntOrNull() ?: 20

        return ok(searchQueryUseCase.search(keyword, sort, lastSearchIndexId, page, size))
    }
}