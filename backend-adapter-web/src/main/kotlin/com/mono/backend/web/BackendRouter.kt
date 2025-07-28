package com.mono.backend.web

import com.mono.backend.web.auth.AuthRouter
import com.mono.backend.web.campsite.CampsiteRouter
import com.mono.backend.web.exrate.ExRateRouter
import com.mono.backend.web.member.MemberRouter
import com.mono.backend.web.notification.NotificationRouter
import com.mono.backend.web.post.PostRouter
import com.mono.backend.web.search.SearchRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class BackendRouter(
    private val exRateRouter: ExRateRouter,
    private val postRouter: PostRouter,
    private val authRouter: AuthRouter,
    private val memberRouter: MemberRouter,
    private val notificationRouter: NotificationRouter,
    private val searchRouter: SearchRouter,
    private val campsiteRouter: CampsiteRouter,
) {
    @Bean
    fun backendRoutes(): RouterFunction<ServerResponse> = coRouter {
        "/v1".nest {
            "/auth".nest(authRouter.authRoutes())
            "/members".nest(memberRouter.memberRoutes())
            "/ex-rate".nest(exRateRouter.exRateRoutes())
            "/posts".nest(postRouter.postRoutes())
            "/notifications".nest(notificationRouter.notificationRoutes())
            "/search".nest(searchRouter.searchRoutes())
            "/campsites".nest(campsiteRouter.campsiteRoutes())
        }
    }
}