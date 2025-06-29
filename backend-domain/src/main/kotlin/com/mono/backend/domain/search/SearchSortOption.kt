package com.mono.backend.domain.search

enum class SearchSortOption {
    LATEST,
    RELEVANCE,
    ;
}

fun String?.toSearchSortOption(): SearchSortOption? {
    return this?.uppercase()?.let { SearchSortOption.valueOf(it) }
}