package com.mono.backend.article

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import kotlin.jvm.optionals.getOrNull

@Component
class ArticleHandler(
    private val articleCommandUseCase: ArticleCommandUseCase,
    private val articleQueryUseCase: ArticleQueryUseCase,
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        return ok(articleQueryUseCase.read(articleId))
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(articleQueryUseCase.readAll(boardId, page, pageSize))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastArticleId = serverRequest.queryParam("lastArticleId").getOrNull()?.toLong()
        return ok(articleQueryUseCase.readAllInfiniteScroll(boardId, lastArticleId, pageSize))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val requestBody = serverRequest.awaitBody(ArticleCreateRequest::class)
        return ok(articleCommandUseCase.create(requestBody))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val requestBody = serverRequest.awaitBody(ArticleUpdateRequest::class)
        return ok(articleCommandUseCase.update(articleId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        articleCommandUseCase.delete(articleId)
        return noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.pathVariable("boardId").toLong()
        return ok(articleQueryUseCase.count(boardId))
    }
}