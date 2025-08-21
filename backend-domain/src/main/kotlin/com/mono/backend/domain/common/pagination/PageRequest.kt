package com.mono.backend.domain.common.pagination

data class PageRequest(
    val page: Long = 0,
    val size: Long = 20,
    val sort: String? = null,
    val direction: String? = null
)
