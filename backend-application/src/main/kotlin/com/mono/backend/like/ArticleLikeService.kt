package com.mono.backend.like

import com.mono.backend.event.EventType
import com.mono.backend.event.article.ArticleEventDispatcherPort
import com.mono.backend.event.payload.ArticleLikedEventPayload
import com.mono.backend.event.payload.ArticleUnlikedEventPayload
import com.mono.backend.like.response.ArticleLikeResponse
import com.mono.backend.persistence.like.ArticleLikeCountPersistencePort
import com.mono.backend.persistence.like.ArticleLikePersistencePort
import com.mono.backend.snowflake.Snowflake
import com.mono.backend.transaction.transaction
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class ArticleLikeService(
    private val articleLikePersistencePort: ArticleLikePersistencePort,
    private val articleLikeCountPersistencePort: ArticleLikeCountPersistencePort,
    private val articleEventDispatcherPort: ArticleEventDispatcherPort
) : ArticleLikeUseCase {
    override suspend fun read(articleId: Long, userId: Long): ArticleLikeResponse? {
        return articleLikePersistencePort.findByArticleIdAndUserId(articleId, userId)?.let {
            ArticleLikeResponse.from(it)
        }
    }

    /**
     * update 구문
     */
    override suspend fun likePessimisticLock1(articleId: Long, userId: Long) = coroutineScope {
        transaction {
            val articleLike = articleLikePersistencePort.save(ArticleLike.from(Snowflake.nextId(), articleId, userId))

            launch {
                articleLikeCountPersistencePort.increase(articleId).takeIf { it == 0 }?.let {
                    /***
                     * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
                     * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
                     */
                    articleLikeCountPersistencePort.save(ArticleLikeCount(articleId, 1L))
                }
            }

            articleEventDispatcherPort.dispatch(
                type = EventType.ARTICLE_LIKED,
                payload = ArticleLikedEventPayload.from(articleLike, count(articleLike.articleId))
            )
        }
    }

    override suspend fun unlikePessimisticLock1(articleId: Long, userId: Long): Unit = coroutineScope {
        transaction {
            articleLikePersistencePort.findByArticleIdAndUserId(articleId, userId)
                ?.let { articleLike ->
                    launch { articleLikePersistencePort.delete(articleLike) }
                    launch { articleLikeCountPersistencePort.decrease(articleId) }

                    articleEventDispatcherPort.dispatch(
                        type = EventType.ARTICLE_UNLIKED,
                        payload = ArticleUnlikedEventPayload.from(articleLike, count(articleLike.articleId))
                    )
                }
        }
    }

    /**
     * select ... for update + update
     */
    override suspend fun likePessimisticLock2(articleId: Long, userId: Long) = coroutineScope {
        transaction {
            launch { articleLikePersistencePort.save(ArticleLike.from(Snowflake.nextId(), articleId, userId)) }

            val articleLikeCount = articleLikeCountPersistencePort.findLockedByArticleId(articleId)
                ?: ArticleLikeCount(articleId, 0L)
            articleLikeCount.increase()
            articleLikeCountPersistencePort.save(articleLikeCount) // find가 안된 경우 새로 생성하기 때문에, save 명시적 호출
        }
    }

    override suspend fun unlikePessimisticLock2(articleId: Long, userId: Long) = coroutineScope {
        transaction {
            articleLikePersistencePort.findByArticleIdAndUserId(articleId, userId)
                ?.let { articleLike ->
                    launch { articleLikePersistencePort.delete(articleLike) }
                    val articleLikeCount = articleLikeCountPersistencePort.findLockedByArticleId(articleId)
                        ?: throw RuntimeException("count not found")
                    articleLikeCount.decrease()
                    articleLikeCountPersistencePort.save(articleLikeCount) // does not need in JPA
                }
        }
    }

    override suspend fun likeOptimisticLock(articleId: Long, userId: Long) = coroutineScope {
        transaction {
            launch { articleLikePersistencePort.save(ArticleLike.from(Snowflake.nextId(), articleId, userId)) }

            val articleLikeCount = articleLikeCountPersistencePort.findById(articleId)
                ?: ArticleLikeCount(articleId, 0L)
            articleLikeCount.increase()
            articleLikeCountPersistencePort.save(articleLikeCount)
        }
    }

    override suspend fun unlikeOptimisticLock(articleId: Long, userId: Long) = coroutineScope {
        transaction {
            articleLikePersistencePort.findByArticleIdAndUserId(articleId, userId)
                ?.let { articleLike ->
                    launch { articleLikePersistencePort.delete(articleLike) }
                    val articleLikeCount = articleLikeCountPersistencePort.findById(articleId)
                        ?: throw RuntimeException("count not found")
                    articleLikeCount.decrease()
                    articleLikeCountPersistencePort.save(articleLikeCount)
                }
        }
    }

    override suspend fun count(articleId: Long): Long {
        return articleLikeCountPersistencePort.findById(articleId)?.likeCount ?: 0
    }
}