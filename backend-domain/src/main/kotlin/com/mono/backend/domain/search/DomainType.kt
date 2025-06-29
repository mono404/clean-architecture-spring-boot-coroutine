package com.mono.backend.domain.search

enum class DomainType(
    val id: Long
) {
    ALL(0),
    POST(1),
    ;

    companion object {
        private val idMap = entries.associateBy(DomainType::id)
        fun fromId(domainId: Long): DomainType = idMap[domainId] ?: ALL
    }
}