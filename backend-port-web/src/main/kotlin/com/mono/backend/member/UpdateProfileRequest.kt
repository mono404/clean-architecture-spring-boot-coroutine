package com.mono.backend.member

data class UpdateProfileRequest(
    val nickname: String,
    val profileImageUrl: String? = null,
)
