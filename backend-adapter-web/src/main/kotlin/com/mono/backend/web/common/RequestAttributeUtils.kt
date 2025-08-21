package com.mono.backend.web.common

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.port.web.exceptions.UnauthorizedException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.attributeOrNull
import org.springframework.web.reactive.function.server.queryParamOrNull

object RequestAttributeUtils {
    fun ServerRequest.longQueryParam(name: String) = queryParamOrNull(name)?.toLongOrNull()

    fun ServerRequest.getMemberId() = attributeOrNull("memberId").toString().toLongOrNull()
        ?: throw UnauthorizedException()

    fun ServerRequest.getMemberIdOrNull() = attributeOrNull("memberId").toString().toLongOrNull()

    fun ServerRequest.getPageRequest() = PageRequest(
        page = longQueryParam("page") ?: 0,
        size = longQueryParam("pageSize") ?: 20,
        sort = queryParamOrNull("sort"),
        direction = queryParamOrNull("direction")
    )

    fun ServerRequest.getCursorRequest() = CursorRequest(
        cursor = queryParamOrNull("cursor"),
        size = longQueryParam("pageSize") ?: 20,
        sort = queryParamOrNull("sort"),
        direction = queryParamOrNull("direction")
    )
}