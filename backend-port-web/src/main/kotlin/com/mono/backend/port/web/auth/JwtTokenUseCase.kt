package com.mono.backend.port.web.auth

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.SocialProvider
import org.springframework.security.core.Authentication
import java.time.LocalDateTime

interface JwtTokenUseCase {
    fun validateToken(token: String): Boolean
    fun getAuthentication(token: String): Authentication
    fun getMemberId(token: String): Long

    suspend fun createAccessToken(member: Member): String
    suspend fun createRefreshToken(member: Member): Pair<String, LocalDateTime>
    suspend fun getProvider(token: String): SocialProvider
    suspend fun getProviderId(token: String): String
}