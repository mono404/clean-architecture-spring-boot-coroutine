package com.mono.backend.member

interface MemberUseCase {
    suspend fun updateProfile(memberId: Long, updateProfileRequest: UpdateProfileRequest)
    suspend fun validateNickname(nickname: String): Boolean
}