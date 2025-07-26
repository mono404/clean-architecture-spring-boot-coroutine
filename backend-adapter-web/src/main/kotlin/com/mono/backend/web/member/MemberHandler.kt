package com.mono.backend.web.member

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.member.dto.UpdateProfileRequest
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class MemberHandler(
    private val memberUseCase: MemberUseCase,
) : DefaultHandler {
    val log = logger()

    suspend fun getMyProfile(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        return ok(memberUseCase.getMember(memberId))
    }

    suspend fun patchMyProfile(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val updateProfileRequest = serverRequest.awaitBodyOrNull(UpdateProfileRequest::class)
            ?: return badRequest("Member update request is null")
        memberUseCase.updateProfile(memberId, updateProfileRequest)
        return noContent()
    }

    suspend fun validateNickname(serverRequest: ServerRequest): ServerResponse {
        val nickname = serverRequest.queryParamOrNull("nickname")
            ?: return ServerResponse.badRequest().bodyValueAndAwait("nickname parameter is required")

        val isValid = memberUseCase.validateNickname(nickname)
        return if (isValid) {
            ok(true)
        } else {
            ok(false)
        }

    }
}