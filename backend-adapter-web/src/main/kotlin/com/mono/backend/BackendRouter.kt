package com.mono.backend

import com.mono.backend.auth.AuthRouter
import com.mono.backend.comment.CommentRouter
import com.mono.backend.comment.CommentRouterV2
import com.mono.backend.exrate.ExRateRouter
import com.mono.backend.fcm.FcmRouter
import com.mono.backend.hotpost.HotPostRouter
import com.mono.backend.like.LikeRouter
import com.mono.backend.member.MemberRouter
import com.mono.backend.post.PostRouter
import com.mono.backend.view.ViewRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class BackendRouter(
    private val exRateRouter: ExRateRouter,
    private val postRouter: PostRouter,
    private val commentRouter: CommentRouter,
    private val commentRouterV2: CommentRouterV2,
    private val likeRouter: LikeRouter,
    private val viewRouter: ViewRouter,
    private val hotPostRouter: HotPostRouter,
    private val authRouter: AuthRouter,
    private val memberRouter: MemberRouter,
    private val fcmRouter: FcmRouter
) {
    @Bean
    fun backendRoutes(): RouterFunction<ServerResponse> = coRouter {
        "/v1".nest {
            "/auth".nest(authRouter.authRoutes())
            "/member".nest(memberRouter.memberRoutes())
            "/ex-rate".nest(exRateRouter.exRateRoutes())
            "/posts".nest(postRouter.postRoutes())
            "/comments".nest(commentRouter.commentRoutes())
            "/post-like".nest {
                "/posts/{postId}".nest(likeRouter.likeRoutes())
            }
            "/post-views".nest {
                "/posts/{postId}".nest(viewRouter.viewRoutes())
            }
            "/hot-posts".nest(hotPostRouter.hotPostRoutes())
            "/fcm-tokens".nest(fcmRouter.fcmRoutes())
        }
        "/v2".nest {
            "/comments".nest(commentRouterV2.commentRoutesV2())
        }
    }
}