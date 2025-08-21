package com.mono.backend.port.infra.oauth.webclient

import com.mono.backend.domain.auth.OAuthMemberInfo
import com.mono.backend.domain.member.SocialProvider

interface OAuthTokenVerifier {
    suspend fun verify(accessToken: String): OAuthMemberInfo
    fun support(provider: SocialProvider): Boolean
}