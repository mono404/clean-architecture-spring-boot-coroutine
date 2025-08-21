package com.mono.backend.common.util

import com.mono.backend.domain.common.pagination.PageRequest

object PageLimitCalculator {
    fun calculatePageLimit(pageRequest: PageRequest, movablePageCount: Long): Long {
        return (((pageRequest.page - 1) / movablePageCount) + 1) * pageRequest.size * movablePageCount + 1
    }
}