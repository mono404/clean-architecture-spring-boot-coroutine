package com.mono.backend.auth

import com.mono.backend.member.Member
import com.mono.backend.member.SocialProvider
import org.springframework.security.core.Authentication
import java.time.Instant

interface JwtTokenUseCase {
    fun validateToken(token: String): Boolean
    fun getAuthentication(token: String): Authentication
    fun getMemberId(token: String): Long

    suspend fun createAccessToken(member: Member): String
    suspend fun createRefreshToken(member: Member): Pair<String, Instant>
    suspend fun getProvider(token: String): SocialProvider
    suspend fun getProviderId(token: String): String
}