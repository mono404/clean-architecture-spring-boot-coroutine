package com.mono.backend.web.post

import com.mono.backend.web.post.comment.CommentRouter
import com.mono.backend.web.post.hot.HotPostRouter
import com.mono.backend.web.post.like.LikeRouter
import com.mono.backend.web.post.view.ViewRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class PostRouter(
    private val postHandler: PostHandler,

    private val commentRouter: CommentRouter,
    private val likeRouter: LikeRouter,
    private val viewRouter: ViewRouter,
    private val hotPostRouter: HotPostRouter,
) {
    @Bean
    fun postRoutes(): CoRouterFunctionDsl.() -> Unit = {
        GET("/infinite-scroll", postHandler::readAllInfiniteScroll)
        GET("/boards/{boardId}/count", postHandler::count)
        "/hot".nest(hotPostRouter.hotPostRoutes())
        "/{postId}".nest {
            GET("", postHandler::read)
            PUT("", postHandler::update)
            DELETE("", postHandler::delete)
            "/comments".nest(commentRouter.postCommentRoutes())
            "/likes".nest(likeRouter.postLikeRoutes())
            "/views".nest(viewRouter.postViewRoutes())
        }
        GET("", postHandler::readAll)
        POST("", postHandler::create)
    }
}