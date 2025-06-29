package com.mono.backend.web.config

import com.mono.backend.common.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class LoggingWebFilter : WebFilter {
    private val log = logger()
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange)
            .doOnSubscribe { log.info("Request received: ${exchange.request.uri}") }
            .doOnError {
                log.error("Request failed: ${exchange.request.uri}")
                log.error(it.message, it)
            }
    }
}