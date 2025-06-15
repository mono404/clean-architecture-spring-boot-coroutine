package com.mono.backend.auth

import com.mono.backend.UpsertResponse

interface AuthUseCase {
    suspend fun loginOrJoin(request: SocialLoginRequest): UpsertResponse<LoginResponse>
    suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse
    suspend fun logout(request: RefreshTokenRequest)
    suspend fun test(): Tokens
}