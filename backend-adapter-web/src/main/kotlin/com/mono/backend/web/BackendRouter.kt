package com.mono.backend.web

import com.mono.backend.web.auth.AuthRouter
import com.mono.backend.web.exrate.ExRateRouter
import com.mono.backend.web.member.MemberRouter
import com.mono.backend.web.notification.NotificationRouter
import com.mono.backend.web.post.PostRouter
import com.mono.backend.web.post.comment.CommentRouter
import com.mono.backend.web.post.hot.HotPostRouter
import com.mono.backend.web.post.like.LikeRouter
import com.mono.backend.web.post.view.ViewRouter
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
    private val commentRouter: CommentRouter,
    private val likeRouter: LikeRouter,
    private val viewRouter: ViewRouter,
    private val hotPostRouter: HotPostRouter,
    private val authRouter: AuthRouter,
    private val memberRouter: MemberRouter,
    private val notificationRouter: NotificationRouter,
    private val searchRouter: SearchRouter,
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
            "/notifications".nest(notificationRouter.notificationRoutes())
            "/search".nest(searchRouter.searchRoutes())
        }
        "/v2".nest {
            "/comments".nest(commentRouter.commentRoutesV2())
        }
    }
}