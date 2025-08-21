package com.mono.backend.port.web.auth

data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceId: String,
)
