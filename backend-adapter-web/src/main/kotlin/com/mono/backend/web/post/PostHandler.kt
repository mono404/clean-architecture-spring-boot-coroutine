package com.mono.backend.web.post

import com.mono.backend.common.log.logger
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.web.post.PostCommandUseCase
import com.mono.backend.port.web.post.PostQueryUseCase
import com.mono.backend.port.web.post.dto.PostCreateRequest
import com.mono.backend.port.web.post.dto.PostUpdateRequest
import com.mono.backend.web.common.DefaultHandler
import com.mono.backend.web.common.RequestAttributeUtils.getCursorRequest
import com.mono.backend.web.common.RequestAttributeUtils.getMemberId
import com.mono.backend.web.common.RequestAttributeUtils.getMemberIdOrNull
import com.mono.backend.web.common.RequestAttributeUtils.getPageRequest
import com.mono.backend.web.common.RequestAttributeUtils.longQueryParam
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitMultipartData

@Component
class PostHandler(
    private val postCommandUseCase: PostCommandUseCase,
    private val postQueryUseCase: PostQueryUseCase
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberIdOrNull()
        val postId = serverRequest.pathVariable("postId").toLong()
        return ok(postQueryUseCase.read(postId, memberId))
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberIdOrNull()
        val boardType = serverRequest.getBoardType()
        val pageRequest = serverRequest.getPageRequest()
        return ok(postQueryUseCase.readAll(memberId, boardType, pageRequest))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberIdOrNull()
        val boardType = serverRequest.getBoardType()
        val cursorRequest = serverRequest.getCursorRequest()
        return ok(postQueryUseCase.readAllInfiniteScroll(memberId, boardType, cursorRequest))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val memberId = serverRequest.getMemberId()
        val parts = serverRequest.awaitMultipartData()
        val request = PostCreateRequest.fromPart(parts)
        val files = parts["mediaFiles"]?.filterIsInstance<FilePart>() ?: emptyList()

        return ok(postCommandUseCase.create(memberId, request, files))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        val requestBody = serverRequest.awaitBody(PostUpdateRequest::class)
        return ok(postCommandUseCase.update(postId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.pathVariable("postId").toLong()
        postCommandUseCase.delete(postId)
        return noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val boardType = serverRequest.getBoardType()
        return ok(postQueryUseCase.count(boardType))
    }

    private fun ServerRequest.getBoardType() = BoardType.fromId(longQueryParam("boardId"))
}