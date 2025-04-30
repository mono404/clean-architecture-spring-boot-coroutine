package com.mono.backend.article

import com.mono.backend.article.response.ArticleReadPageResponse
import com.mono.backend.article.response.ArticleReadResponse
import com.mono.backend.cache.article.ArticleIdListCachePort
import com.mono.backend.cache.article.ArticleQueryModelCachePort
import com.mono.backend.cache.article.BoardArticleCountCachePort
import com.mono.backend.cache.cache
import com.mono.backend.comment.CommentV2UseCase
import com.mono.backend.like.ArticleLikeUseCase
import com.mono.backend.log.logger
import com.mono.backend.view.ArticleViewUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleQueryService(
    private val articleCommandService: ArticleCommandService,
    private val articleQueryModelCachePort: ArticleQueryModelCachePort,
    private val articleIdListCachePort: ArticleIdListCachePort,
    private val boardArticleCountCachePort: BoardArticleCountCachePort,
    private val viewUseCase: ArticleViewUseCase,
    private val commentV2UseCase: CommentV2UseCase,
    private val likeUseCase: ArticleLikeUseCase,
) : ArticleQueryUseCase {
    private final val log = logger()

    override suspend fun read(articleId: Long): ArticleReadResponse = coroutineScope {
        val articleQueryModel = async {
            articleQueryModelCachePort.read(articleId)
                ?: fetch(articleId)
                ?: throw RuntimeException("Article not found")
        }

        val viewCount = async {
            cache(args = listOf(articleId), type = "articleViewCount", ttlSeconds = 1) {
                viewUseCase.count(articleId)
            }
        }

        ArticleReadResponse.from(articleQueryModel.await(), viewCount.await())
    }

    private suspend fun fetch(articleId: Long): ArticleQueryModel? = coroutineScope {
        val commentCount = async { commentV2UseCase.count(articleId) }
        val likeCount = async { likeUseCase.count(articleId) }
        val articleQueryModel = articleCommandService.read(articleId)
            ?.toDomain(commentCount.await(), likeCount.await())

        launch { articleQueryModel?.let { articleQueryModelCachePort.create(it, Duration.ofDays(1)) } }
        log.info("[ArticleReadService.fetch] fetch data. articleId=$articleId, isPresent=${articleQueryModel != null}")
        articleQueryModel
    }

    override suspend fun readAll(boardId: Long, page: Long, pageSize: Long): ArticleReadPageResponse = coroutineScope {
        val articles = async { readAll(readAllArticleIds(boardId, page, pageSize)) }
        val articleCount = async { count(boardId) }
        ArticleReadPageResponse(
            articles.await(),
            articleCount.await()
        )
    }

    private suspend fun readAll(articleIds: List<Long>): List<ArticleReadResponse> {
        val articleQueryModelMap = articleQueryModelCachePort.readAll(articleIds)?.associateBy { it.articleId }
        return articleIds.map { articleId ->
            if (articleQueryModelMap?.containsKey(articleId) == true) articleQueryModelMap[articleId]
            else fetch(articleId)
        }
            .mapNotNull { articleQueryModel ->
                articleQueryModel?.let {
                    val viewCount = cache(args = listOf(it.articleId), type = "articleViewCount", ttlSeconds = 1) {
                        viewUseCase.count(it.articleId)
                    }
                    ArticleReadResponse.from(it, viewCount)
                }
            }
    }

    private suspend fun readAllArticleIds(boardId: Long, page: Long, pageSize: Long): List<Long> {
        val articleIds = articleIdListCachePort.readAll(boardId, (page - 1) * pageSize, pageSize) ?: emptyList()
        if (pageSize == articleIds.size.toLong()) {
            log.info("[ArticleReadService.readAllArticleIds] return redis data. ")
            return articleIds
        }
        log.info("[ArticleReadService.readAllArticleIds] return origin data.")
        return articleCommandService.readAll(boardId, page, pageSize).articles.map { it.articleId }
    }

    override suspend fun count(boardId: Long): Long {
        val result = boardArticleCountCachePort.read(boardId)
        return result ?: articleCommandService.count(boardId).also {
            boardArticleCountCachePort.createOrUpdate(boardId, it)
        }
    }

    override suspend fun readAllInfiniteScroll(
        boardId: Long,
        lastArticleId: Long?,
        pageSize: Long
    ): List<ArticleReadResponse> {
        return readAll(readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize))
    }

    private suspend fun readAllInfiniteScrollArticleIds(
        boardId: Long,
        lastArticleId: Long?,
        pageSize: Long
    ): List<Long> {
        val articleIds = articleIdListCachePort.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
        if (pageSize == articleIds?.size?.toLong()) {
            log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data. ")
            return articleIds
        }

        log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data.")
        return articleCommandService.readAllInfiniteScroll(boardId, lastArticleId, pageSize).map { it.articleId }
    }
}