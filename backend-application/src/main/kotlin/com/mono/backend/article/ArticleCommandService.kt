package com.mono.backend.article

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.article.response.ArticlePageResponse
import com.mono.backend.article.response.ArticleResponse
import com.mono.backend.event.EventType
import com.mono.backend.event.article.ArticleEventDispatcherPort
import com.mono.backend.event.payload.ArticleCreatedEventPayload
import com.mono.backend.event.payload.ArticleDeletedEventPayload
import com.mono.backend.event.payload.ArticleUpdatedEventPayload
import com.mono.backend.persistence.article.ArticlePersistencePort
import com.mono.backend.persistence.article.BoardArticleCountPersistencePort
import com.mono.backend.snowflake.Snowflake
import com.mono.backend.util.PageLimitCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class ArticleCommandService(
    private val articlePersistencePort: ArticlePersistencePort,
    private val boardArticleCountPersistencePort: BoardArticleCountPersistencePort,
    private val articleEventDispatcherPort: ArticleEventDispatcherPort,
) : ArticleCommandUseCase {
    override suspend fun create(request: ArticleCreateRequest): ArticleResponse = coroutineScope {
        val articleDeferred = async { articlePersistencePort.save(request.toDomain(Snowflake.nextId())) }

        launch {
            boardArticleCountPersistencePort.increase(request.boardId).takeIf { it == 0 }?.let {
                boardArticleCountPersistencePort.save(BoardArticleCount(request.boardId, 1L))
            }
        }

        val article = articleDeferred.await()

        articleEventDispatcherPort.dispatch(
            type = EventType.ARTICLE_CREATED,
            payload = ArticleCreatedEventPayload.from(article, count(article.boardId))
        )
        ArticleResponse.from(article)
    }

    override suspend fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articlePersistencePort.findById(articleId) ?: throw RuntimeException("Article not found")
        val updatedArticle = article.copy(title = request.title, content = request.content)
        articlePersistencePort.save(updatedArticle) // does not need in JPA

        articleEventDispatcherPort.dispatch(
            type = EventType.ARTICLE_UPDATED,
            payload = ArticleUpdatedEventPayload.from(article)
        )

        return ArticleResponse.from(updatedArticle)
    }

    suspend fun read(articleId: Long) = articlePersistencePort.findById(articleId)?.let { ArticleResponse.from(it) }

    //    @Transactional
    override suspend fun delete(articleId: Long) {
        articlePersistencePort.findById(articleId)?.let { article ->
            coroutineScope {
                launch { articlePersistencePort.delete(article) }
                launch { boardArticleCountPersistencePort.decrease(article.boardId) }
            }

            articleEventDispatcherPort.dispatch(
                type = EventType.ARTICLE_DELETED,
                payload = ArticleDeletedEventPayload.from(article, count(article.boardId))
            )
        }
    }

    suspend fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse = coroutineScope {
        val articles = async {
            articlePersistencePort.findAll(boardId, (page - 1) * pageSize, pageSize).map(ArticleResponse::from)
        }
        val articleCount = async {
            articlePersistencePort.count(boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        }
        ArticlePageResponse(
            articles.await(),
            articleCount.await()
        )
    }

    suspend fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, pageSize: Long): List<ArticleResponse> {
        val articles = if (lastArticleId == null)
            articlePersistencePort.findAllInfiniteScroll(boardId, pageSize)
        else
            articlePersistencePort.findAllInfiniteScroll(boardId, pageSize, lastArticleId)

        return articles.map(ArticleResponse::from)
    }

    override suspend fun count(boardId: Long): Long {
        return boardArticleCountPersistencePort.findById(boardId)?.articleCount ?: 0
    }
}