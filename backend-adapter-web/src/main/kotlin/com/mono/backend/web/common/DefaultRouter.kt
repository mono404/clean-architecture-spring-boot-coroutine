package com.mono.backend.web.common

import com.mono.backend.port.web.exceptions.UnauthorizedException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.attributeOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

interface DefaultRouter {
    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        f: suspend (Long, ServerRequest) -> ServerResponse
    ) = withMemberId(function, "", f)

    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        pattern: String,
        f: suspend (Long, ServerRequest) -> ServerResponse
    ) {
        return function(pattern) { serverRequest ->
            val memberId = serverRequest.attributeOrNull("memberId").toString().toLongOrNull()
                ?: throw UnauthorizedException()
            f(memberId, serverRequest)
        }
    }

    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        f: suspend (ServerRequest) -> ServerResponse
    ) = withMemberId(function, "", f)

    fun withMemberId(
        function: (pattern: String, f: suspend (ServerRequest) -> ServerResponse) -> Unit,
        pattern: String,
        f: suspend (ServerRequest) -> ServerResponse
    ) {
        return function(pattern) { serverRequest ->
            val memberId = serverRequest.attributeOrNull("memberId").toString().toLongOrNull()
                ?: throw UnauthorizedException()
            f(serverRequest)
        }
    }

    fun requireRole(role: String): suspend (ServerRequest, suspend (ServerRequest) -> ServerResponse) -> ServerResponse {
        return { request, next ->
            val roles = request.attribute("roles").orElse(emptyList<String>()) as List<String>
            val tmp = request.attributeOrNull("roles").toString()
            println(tmp)

            if (role in roles) {
                next(request)
            } else {
                ServerResponse.status(403).bodyValueAndAwait("Forbidden: $role required")
            }
        }
    }
}