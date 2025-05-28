package com.mono.backend.webclient.oauth

import com.mono.backend.auth.OAuthMemberInfo
import com.mono.backend.member.SocialProvider

interface OAuthTokenVerifier {
    suspend fun verify(accessToken: String): OAuthMemberInfo
    fun support(provider: SocialProvider): Boolean
}