package com.mono.backend.auth

import com.mono.backend.exceptions.UnauthorizedException
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUseCase: JwtTokenUseCase
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = resolveToken(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))

        return try {
            if (token != null && jwtTokenUseCase.validateToken(token)) {
                val auth = jwtTokenUseCase.getAuthentication(token)
                val memberId = jwtTokenUseCase.getMemberId(token)
                exchange.attributes["memberId"] = memberId

                val context = ReactiveSecurityContextHolder.withAuthentication(auth)
                chain.filter(exchange).contextWrite(context)
            } else {
                chain.filter(exchange)
            }
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid token")
        }
    }

    private fun resolveToken(header: String?) = header?.takeIf { it.startsWith("Bearer ") }?.substring(7)
}