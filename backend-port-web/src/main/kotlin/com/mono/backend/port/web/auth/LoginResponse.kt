package com.mono.backend.port.web.auth

import com.mono.backend.port.web.member.dto.MemberResponse

data class LoginResponse(
    val tokens: Tokens,
    val member: MemberResponse
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
)