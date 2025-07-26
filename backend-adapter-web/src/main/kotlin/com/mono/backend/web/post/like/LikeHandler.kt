package com.mono.backend.web.post.like

import com.mono.backend.common.log.logger
import com.mono.backend.port.web.post.like.PostLikeUseCase
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class LikeHandler(
    private val postLikeUseCase: PostLikeUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        return postLikeUseCase.read(postId, memberId)?.let { ok(it) } ?: noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postLikeUseCase.count(postId))
    }

    suspend fun likePessimisticLock1(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postLikeUseCase.likePessimisticLock1(postId, memberId))
    }

    suspend fun unlikePessimisticLock1(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        postLikeUseCase.unlikePessimisticLock1(postId, memberId)
        return noContent()
    }

    suspend fun likePessimisticLock2(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postLikeUseCase.likePessimisticLock2(postId, memberId))
    }

    suspend fun unlikePessimisticLock2(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        postLikeUseCase.unlikePessimisticLock2(postId, memberId)
        return noContent()
    }

    suspend fun likeOptimisticLock(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postLikeUseCase.likeOptimisticLock(postId, memberId))
    }

    suspend fun unlikeOptimisticLock(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val postId = serverRequest.pathVariable("postId").toLong()
        postLikeUseCase.unlikeOptimisticLock(postId, memberId)
        return noContent()
    }
}