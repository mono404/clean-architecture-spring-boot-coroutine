package com.mono.backend.auth

data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceId: String,
)
