package com.mono.backend.port.web.member

import com.mono.backend.port.web.member.dto.UpdateProfileRequest

interface MemberUseCase {
    suspend fun updateProfile(memberId: Long, updateProfileRequest: UpdateProfileRequest)
    suspend fun validateNickname(nickname: String): Boolean
}