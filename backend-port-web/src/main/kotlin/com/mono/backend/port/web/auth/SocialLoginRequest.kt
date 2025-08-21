package com.mono.backend.port.web.auth

import com.mono.backend.domain.member.SocialProvider

data class SocialLoginRequest(
    val provider: SocialProvider,
    val accessToken: String,
    val deviceId: String,
    val idToken: String?
)