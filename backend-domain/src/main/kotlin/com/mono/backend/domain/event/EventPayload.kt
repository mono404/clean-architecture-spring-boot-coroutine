package com.mono.backend.domain.event

import com.mono.backend.domain.search.SearchIndex

interface EventPayload {
    fun toSearchIndex(): SearchIndex? = null
}