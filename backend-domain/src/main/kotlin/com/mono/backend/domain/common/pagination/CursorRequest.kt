package com.mono.backend.domain.common.pagination

data class CursorRequest(
    val cursor: String? = null,
    val size: Long = 20,
    val sort: String? = null,
    val direction: String? = null
)
