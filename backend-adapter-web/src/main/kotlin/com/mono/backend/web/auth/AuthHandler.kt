package com.mono.backend.web.auth

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.auth.AuthUseCase
import com.mono.backend.port.web.auth.RefreshTokenRequest
import com.mono.backend.port.web.auth.SocialLoginRequest
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody

@Component
class AuthHandler(
    private val authUseCase: AuthUseCase
) : DefaultHandler {
    private val log = logger()

    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        val socialLoginRequest = serverRequest.awaitBody(SocialLoginRequest::class)
        return authUseCase.loginOrJoin(socialLoginRequest).let {
            log.info("[AuthHandler.login] member token {}", it.typeText)
            if (it.isCreate) created("", it) else ok(it)
        }
    }

    suspend fun refresh(serverRequest: ServerRequest): ServerResponse {
        val refreshTokenRequest = serverRequest.awaitBody(RefreshTokenRequest::class)
        val response = authUseCase.refreshToken(refreshTokenRequest)
        return ok(response)
    }

    suspend fun logout(serverRequest: ServerRequest): ServerResponse {
        val refreshTokenRequest = serverRequest.awaitBody(RefreshTokenRequest::class)
        authUseCase.logout(refreshTokenRequest)
        return noContent()
    }

    suspend fun test(serverRequest: ServerRequest): ServerResponse {
        return ok(authUseCase.test())
    }
}