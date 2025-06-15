package com.mono.backend.post

import com.mono.backend.cache.cache
import com.mono.backend.cache.post.BoardPostCountCachePort
import com.mono.backend.cache.post.PostIdListCachePort
import com.mono.backend.cache.post.PostQueryModelCachePort
import com.mono.backend.comment.CommentV2UseCase
import com.mono.backend.like.PostLikeUseCase
import com.mono.backend.log.logger
import com.mono.backend.post.response.PostReadPageResponse
import com.mono.backend.post.response.PostReadResponse
import com.mono.backend.view.PostViewUseCase
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
            cache(args = listOf(postId), type = "postViewCount", ttlSeconds = 1) {
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

    override suspend fun readAll(boardId: Long, page: Long, pageSize: Long): PostReadPageResponse = coroutineScope {
        val posts = async { readAll(readAllPostIds(boardId, page, pageSize)) }
        val postCount = async { count(boardId) }
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
                    val viewCount = cache(args = listOf(it.postId), type = "articleViewCount", ttlSeconds = 1) {
                        viewUseCase.count(it.postId)
                    }
                    PostReadResponse.from(it, viewCount)
                }
            }
    }

    private suspend fun readAllPostIds(boardId: Long, page: Long, pageSize: Long): List<Long> {
        val postIds = postIdListCachePort.readAll(boardId, (page - 1) * pageSize, pageSize) ?: emptyList()
        if (pageSize == postIds.size.toLong()) {
            log.info("[PostReadService.readAllPostIds] return redis data. ")
            return postIds
        }
        log.info("[PostReadService.readAllPostIds] return origin data.")
        return postCommandService.readAll(boardId, page, pageSize).posts.map { it.postId.toLong() }
    }

    override suspend fun count(boardId: Long): Long {
        val result = boardPostCountCachePort.read(boardId)
        return result ?: postCommandService.count(boardId).also {
            boardPostCountCachePort.createOrUpdate(boardId, it)
        }
    }

    override suspend fun readAllInfiniteScroll(
        boardId: Long,
        lastPostId: Long?,
        pageSize: Long
    ): List<PostReadResponse> {
        return readAll(readAllInfiniteScrollPostIds(boardId, lastPostId, pageSize))
    }

    private suspend fun readAllInfiniteScrollPostIds(
        boardId: Long,
        lastPostId: Long?,
        pageSize: Long
    ): List<Long> {
        val postIds = postIdListCachePort.readAllInfiniteScroll(boardId, lastPostId, pageSize)
        if (pageSize == postIds?.size?.toLong()) {
            log.info("[PostReadService.readAllInfiniteScrollPostIds] return redis data. ")
            return postIds
        }

        log.info("[PostReadService.readAllInfiniteScrollPostIds] return origin data.")
        return postCommandService.readAllInfiniteScroll(boardId, lastPostId, pageSize).map { it.postId.toLong() }
    }
}