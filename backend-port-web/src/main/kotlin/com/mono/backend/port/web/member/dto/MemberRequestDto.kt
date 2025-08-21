package com.mono.backend.port.web.member.dto

import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class UpdateProfileRequest(
    val nickname: String
) {
    companion object {
        fun fromPart(parts: MultiValueMap<String, Part>) = UpdateProfileRequest(
            nickname = (parts["nickname"]?.firstOrNull() as? FormFieldPart)?.value() ?: ""
        )
    }
}