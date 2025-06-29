package com.mono.backend.web.member

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.member.dto.UpdateProfileRequest
import com.mono.backend.web.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import kotlin.jvm.optionals.getOrNull

@Component
class MemberHandler(
    private val memberUseCase: MemberUseCase,
) : DefaultHandler {
    val log = logger()

    suspend fun patchMyProfile(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val updateProfileRequest = serverRequest.awaitBodyOrNull(UpdateProfileRequest::class)
            ?: return badRequest("Member update request is null")
        memberUseCase.updateProfile(memberId, updateProfileRequest)
        return noContent()
    }

    suspend fun validateNickname(serverRequest: ServerRequest): ServerResponse {
        val nickname = serverRequest.queryParam("nickname").getOrNull()
            ?: return ServerResponse.badRequest().bodyValueAndAwait("nickname parameter is required")

        val isValid = memberUseCase.validateNickname(nickname)
        return if (isValid) {
            ok(true)
        } else {
            ok(false)
        }

    }
}