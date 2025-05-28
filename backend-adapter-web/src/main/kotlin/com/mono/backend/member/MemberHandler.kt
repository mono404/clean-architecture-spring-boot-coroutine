package com.mono.backend.member

import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import kotlin.jvm.optionals.getOrNull

@Component
class MemberHandler(
    private val memberUseCase: MemberUseCase,
) : DefaultHandler {
    val log = logger()

    suspend fun patchMyProfile(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.attribute("memberId").get() as Long
        val updateProfileRequest = serverRequest.awaitBodyOrNull(UpdateProfileRequest::class)
            ?: return ServerResponse.badRequest().buildAndAwait()
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