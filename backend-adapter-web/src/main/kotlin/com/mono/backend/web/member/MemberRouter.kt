package com.mono.backend.web.member

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class MemberRouter(
    private val memberHandler: MemberHandler
) {
    @Bean
    fun memberRoutes(): CoRouterFunctionDsl.() -> Unit = {
        PATCH("/me", memberHandler::patchMyProfile)
        GET("/validate-nickname", memberHandler::validateNickname)
    }
}