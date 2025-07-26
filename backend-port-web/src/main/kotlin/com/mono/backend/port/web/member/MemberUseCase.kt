package com.mono.backend.port.web.member

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.port.web.member.dto.MemberResponse
import com.mono.backend.port.web.member.dto.UpdateProfileRequest

interface MemberUseCase {
    suspend fun updateProfile(memberId: Long, updateProfileRequest: UpdateProfileRequest)
    suspend fun validateNickname(nickname: String): Boolean
    suspend fun getEmbeddedMember(memberId: Long): EmbeddedMember
    suspend fun getMember(memberId: Long): MemberResponse
}