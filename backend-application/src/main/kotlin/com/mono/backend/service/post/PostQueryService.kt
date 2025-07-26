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
import com.mono.backend.port.infra.post.persistence.BoardPostCountPersistencePort
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import com.mono.backend.port.web.post.PostQueryUseCase
import com.mono.backend.port.web.post.comment.CommentUseCase
import com.mono.backend.port.web.post.dto.PostReadPageResponse
import com.mono.backend.port.web.post.dto.PostReadResponse
import com.mono.backend.port.web.post.like.PostLikeUseCase
import com.mono.backend.port.web.post.view.PostViewUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PostQueryService(
    private val postPersistencePort: PostPersistencePort,
    private val boardPostCountPersistencePort: BoardPostCountPersistencePort,
    private val postQueryModelCachePort: PostQueryModelCachePort,
    private val postIdListCachePort: PostIdListCachePort,
    private val boardPostCountCachePort: BoardPostCountCachePort,
    private val viewUseCase: PostViewUseCase,
    private val commentUseCase: CommentUseCase,
    private val likeUseCase: PostLikeUseCase,
) : PostQueryUseCase {
    private final val log = logger()

    override suspend fun read(postId: Long, memberId: Long?): PostReadResponse = coroutineScope {
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
        val liked = async { memberId?.let { likeUseCase.read(postId, memberId) } != null }

        PostReadResponse.from(postQueryModel.await(), viewCount.await(), liked.await())
    }

    override suspend fun readAll(
        memberId: Long?,
        boardType: BoardType,
        pageRequest: PageRequest
    ): PostReadPageResponse =
        coroutineScope {
            val postIds = readAllPostIds(boardType, pageRequest)

            val posts = async { readAll(postIds, memberId) }
            val postCount = async { count(boardType) }

            PostReadPageResponse(posts.await(), postCount.await())
        }

    override suspend fun readAllInfiniteScroll(
        memberId: Long?,
        boardType: BoardType,
        cursorRequest: CursorRequest
    ): List<PostReadResponse> {
        val postIds = readAllInfiniteScrollPostIds(boardType, cursorRequest)
        return readAll(postIds, memberId)
    }

    override suspend fun count(boardType: BoardType): Long {
        return boardPostCountCachePort.read(boardType) ?: (boardPostCountPersistencePort.findById(boardType)?.postCount
            ?: 0).also {
            boardPostCountCachePort.createOrUpdate(boardType, it)
        }
    }

    private suspend fun readAll(postIds: List<Long>, memberId: Long?): List<PostReadResponse> {
        val postQueryModelMap = postQueryModelCachePort.readAll(postIds).associateBy { it.postId }
            .also { log.info("[PostQueryService.readAll] return redis data ${it.keys}") }

        val fetchedMap = postIds.filterNot(postQueryModelMap::containsKey)
            .takeIf { it.isNotEmpty() }
            ?.let { missedIds ->
                log.info("[PostQueryService.readAll] fetch data $missedIds")
                fetchAllByIds(missedIds).associateBy { it.postId }
            } ?: emptyMap()

        val fullMap = postQueryModelMap + fetchedMap
        val likedMap = memberId?.let { likeUseCase.readAll(postIds, memberId) } ?: emptyMap()

        return postIds.mapNotNull { postId ->
            fullMap[postId]?.let {
                val viewCount = cache(
                    args = listOf(it.postId),
                    type = "post-view-count",
                    ttlSeconds = 1,
                ) {
                    viewUseCase.count(it.postId)
                }
                val liked = likedMap[postId] != null

                PostReadResponse.from(it, viewCount, liked)
            }
        }
    }

    private suspend fun readAllPostIds(boardType: BoardType, pageRequest: PageRequest): List<Long> {
        val cachedIds = postIdListCachePort.readAll(boardType, pageRequest) ?: emptyList()
        if (pageRequest.size == cachedIds.size.toLong()) {
            log.info("[PostQueryService.readAllPostIds] return redis data. ")
            return cachedIds
        }
        val postIdsFromOrigin = postPersistencePort.findAll(boardType, pageRequest).map { it.postId }
        postIdListCachePort.saveAll(boardType, postIdsFromOrigin)
        log.info("[PostQueryService.readAllPostIds] return origin data.")
        return postIdsFromOrigin
    }

    private suspend fun readAllInfiniteScrollPostIds(
        boardType: BoardType,
        cursorRequest: CursorRequest
    ): List<Long> {
        val postIds = postIdListCachePort.readAllInfiniteScroll(boardType, cursorRequest) ?: emptyList()
        if (cursorRequest.size == postIds.size.toLong()) {
            log.info("[PostQueryService.readAllInfiniteScrollPostIds] return redis data. ")
            return postIds
        }
        val postIdsFromOrigin = postPersistencePort.findAllInfiniteScroll(boardType, cursorRequest)
            .map { it.postId }
        postIdListCachePort.saveAll(boardType, postIdsFromOrigin)
        log.info("[PostQueryService.readAllInfiniteScrollPostIds] return origin data.")
        return postIdsFromOrigin
    }

    private suspend fun fetch(postId: Long): PostQueryModel? = coroutineScope {
        val commentCount = async { commentUseCase.count(postId) }
        val likeCount = async { likeUseCase.count(postId) }
        val post = postPersistencePort.findById(postId)
        val postQueryModel = post?.let {
            PostQueryModel(
                postId = it.postId,
                title = it.title,
                content = it.content,
                boardType = it.boardType,
                createdAt = it.createdAt ?: LocalDateTime.now(),
                updatedAt = it.updatedAt ?: LocalDateTime.now(),
                postCommentCount = commentCount.await(),
                postLikeCount = likeCount.await(),
                member = it.member
            )
        }

        launch { postQueryModel?.let { postQueryModelCachePort.create(it) } }
        log.info("[PostQueryService.fetch] fetch data. postId=$postId, isPresent=${postQueryModel != null}")
        postQueryModel
    }

    private suspend fun fetchAllByIds(postIds: List<Long>): List<PostQueryModel> = coroutineScope {
        val commentsDeferred = async { commentUseCase.countAll(postIds) }
        val likesDeferred = async { likeUseCase.countAll(postIds) }
        val postsDeferred = async { postPersistencePort.findAllByIds(postIds) }

        val comments = commentsDeferred.await()
        val likes = likesDeferred.await()
        val posts = postsDeferred.await()

        val result = posts.map { post ->
            val commentCount = comments[post.postId] ?: 0
            val likeCount = likes[post.postId] ?: 0
            PostQueryModel(
                postId = post.postId,
                title = post.title,
                content = post.content,
                boardType = post.boardType,
                createdAt = post.createdAt ?: LocalDateTime.now(),
                updatedAt = post.updatedAt ?: LocalDateTime.now(),
                postCommentCount = commentCount,
                postLikeCount = likeCount,
                member = post.member
            )
        }

        log.info("[PostQueryService.fetch] fetch data. postIds={}", postIds)
        launch { postQueryModelCachePort.createAll(result) }
        result
    }
}