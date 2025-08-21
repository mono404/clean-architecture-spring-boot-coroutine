package com.mono.backend.port.web.member

import com.mono.backend.domain.common.member.EmbeddedMember
import com.mono.backend.port.web.member.dto.MemberResponse
import com.mono.backend.port.web.member.dto.UpdateProfileRequest
import org.springframework.http.codec.multipart.FilePart

interface MemberUseCase {
    suspend fun updateProfile(memberId: Long, updateProfileRequest: UpdateProfileRequest, profileImage: FilePart?)
    suspend fun validateNickname(nickname: String): Boolean
    suspend fun getEmbeddedMember(memberId: Long): EmbeddedMember
    suspend fun getMember(memberId: Long): MemberResponse
}