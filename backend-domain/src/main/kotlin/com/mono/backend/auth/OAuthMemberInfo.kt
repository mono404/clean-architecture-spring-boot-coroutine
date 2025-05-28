package com.mono.backend.auth

import com.mono.backend.member.SocialProvider

data class OAuthMemberInfo(
    val providerId: String,
    val provider: SocialProvider
)
