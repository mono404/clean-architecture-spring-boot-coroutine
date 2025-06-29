package com.mono.backend.port.web.member.dto

data class UpdateProfileRequest(
    val nickname: String,
    val profileImageUrl: String? = null,
)