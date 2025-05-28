package com.mono.backend.auth

import com.mono.backend.member.SocialProvider

data class SocialLoginRequest(
    val provider: SocialProvider,
    val accessToken: String,
    val deviceId: String,
    val idToken: String?
)