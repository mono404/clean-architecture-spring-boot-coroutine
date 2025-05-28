package com.mono.backend.auth

import com.mono.backend.member.MemberResponse

data class LoginResponse(
    val tokens: Tokens,
    val member: MemberResponse
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
)