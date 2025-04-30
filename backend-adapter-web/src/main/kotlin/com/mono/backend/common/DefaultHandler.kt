package com.mono.backend.common

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.json
import org.springframework.web.util.UriComponentsBuilder

interface DefaultHandler {
    suspend fun ok(body: Any): ServerResponse = ServerResponse
        .ok()
        .json()
        .bodyValue(body)
        .awaitSingle()

    suspend fun created(uri: String, body: Any): ServerResponse = ServerResponse
        .created(UriComponentsBuilder.fromPath(uri).build().toUri())
        .json()
        .bodyValue(body)
        .awaitSingle()

    suspend fun noContent(): ServerResponse = ServerResponse
        .noContent()
        .buildAndAwait()
}