package com.mono.backend.domain.auth

import com.mono.backend.domain.member.SocialProvider

data class OAuthMemberInfo(
    val providerId: String,
    val provider: SocialProvider
)
