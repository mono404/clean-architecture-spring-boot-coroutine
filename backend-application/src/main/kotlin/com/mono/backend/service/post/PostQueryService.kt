package com.mono.backend.service.post

import com.mono.backend.common.log.logger
import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.PostQueryModel
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.common.cache.cache
import com.mono.backend.port.infra.post.cache.BoardPostCountCachePort
import com.mono.backend.port.infra.post.cache.PostIdListCachePort
import com.mono.backend.port.infra.post.cache.PostQueryModelCachePort
import com.mono.backend.port.web.post.PostQueryUseCase
import com.mono.backend.port.web.post.comment.CommentV2UseCase
import com.mono.backend.port.web.post.dto.PostReadPageResponse
import com.mono.backend.port.web.post.dto.PostReadResponse
import com.mono.backend.port.web.post.like.PostLikeUseCase
import com.mono.backend.port.web.post.view.PostViewUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PostQueryService(
    private val postCommandService: PostCommandService,
    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
    private val viewUseCase: PostViewUseCase,
    private val commentV2UseCase: CommentV2UseCase,
    private val likeUseCase: PostLikeUseCase,
) : PostQueryUseCase {
    private final val log = logger()

    override suspend fun read(postId: Long): PostReadResponse = coroutineScope {
        val postQueryModel = async {
            postQueryModelCachePort.read(postId)
                ?: fetch(postId)
                ?: throw RuntimeException("Post not found")
        }

        val viewCount = async {
            cache(
                args = listOf(postId),
                type = "postViewCount",
                ttlSeconds = 1
            ) {
                viewUseCase.count(postId)
            }
        }

        PostReadResponse.from(postQueryModel.await(), viewCount.await())
    }

    private suspend fun fetch(postId: Long): PostQueryModel? = coroutineScope {
        val commentCount = async { commentV2UseCase.count(postId) }
        val likeCount = async { likeUseCase.count(postId) }
        val postQueryModel = postCommandService.read(postId)
            ?.toDomain(commentCount.await(), likeCount.await())

        launch { postQueryModel?.let { postQueryModelCachePort.create(it, Duration.ofDays(1)) } }
        log.info("[PostReadService.fetch] fetch data. postId=$postId, isPresent=${postQueryModel != null}")
        postQueryModel
    }

    override suspend fun readAll(boardType: BoardType, pageRequest: PageRequest): PostReadPageResponse =
        coroutineScope {
            val posts = async { readAll(readAllPostIds(boardType, pageRequest)) }
            val postCount = async { count(boardType) }
        PostReadPageResponse(
            posts.await(),
            postCount.await()
        )
    }

    private suspend fun readAll(postIds: List<Long>): List<PostReadResponse> {
        val postQueryModelMap = postQueryModelCachePort.readAll(postIds)?.associateBy { it.postId }
        return postIds.map { postId ->
            if (postQueryModelMap?.containsKey(postId) == true) postQueryModelMap[postId]
            else fetch(postId)
        }
            .mapNotNull { articleQueryModel ->
                articleQueryModel?.let {
                    val viewCount = cache(
                        args = listOf(it.postId),
                        type = "articleViewCount",
                        ttlSeconds = 1
                    ) {
                        viewUseCase.count(it.postId)
                    }
                    PostReadResponse.from(it, viewCount)
                }
            }
    }

    private suspend fun readAllPostIds(boardId: BoardType, pageRequest: PageRequest): List<Long> {
        val postIds = postIdListCachePort.readAll(boardId, pageRequest) ?: emptyList()
        if (pageRequest.size == postIds.size.toLong()) {
            log.info("[PostReadService.readAllPostIds] return redis data. ")
            return postIds
        }
        log.info("[PostReadService.readAllPostIds] return origin data.")
        return postCommandService.readAll(boardId, pageRequest).posts.map { it.postId.toLong() }
    }

    override suspend fun count(boardType: BoardType): Long {
        val result = boardPostCountCachePort.read(boardType)
        return result ?: postCommandService.count(boardType).also {
            boardPostCountCachePort.createOrUpdate(boardType, it)
        }
    }

    override suspend fun readAllInfiniteScroll(
        boardType: BoardType,
        cursorRequest: CursorRequest
    ): List<PostReadResponse> {
        return readAll(readAllInfiniteScrollPostIds(boardType, cursorRequest))
    }

    private suspend fun readAllInfiniteScrollPostIds(
        boardType: BoardType,
        cursorRequest: CursorRequest
    ): List<Long> {
        val postIds = postIdListCachePort.readAllInfiniteScroll(boardType, cursorRequest)
        if (cursorRequest.size == postIds?.size?.toLong()) {
            log.info("[PostReadService.readAllInfiniteScrollPostIds] return redis data. ")
            return postIds
        }

        log.info("[PostReadService.readAllInfiniteScrollPostIds] return origin data.")
        return postCommandService.readAllInfiniteScroll(boardType, cursorRequest).map { it.postId.toLong() }
    }
}