package com.mono.backend.service.post.like

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostLikedEventPayload
import com.mono.backend.domain.event.payload.PostUnlikedEventPayload
import com.mono.backend.domain.post.like.PostLike
import com.mono.backend.domain.post.like.PostLikeCount
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.like.persistence.PostLikeCountPersistencePort
import com.mono.backend.port.infra.like.persistence.PostLikePersistencePort
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import com.mono.backend.port.web.post.like.PostLikeUseCase
import com.mono.backend.port.web.post.like.dto.PostLikeResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class PostLikeService(
    private val postLikePersistencePort: PostLikePersistencePort,
    private val postLikeCountPersistencePort: PostLikeCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort
) : PostLikeUseCase {
    override suspend fun read(postId: Long, memberId: Long): PostLikeResponse? {
        return postLikePersistencePort.findByPostIdAndMemberId(postId, memberId)?.let {
            PostLikeResponse.from(it)
        }
    }

    /**
     * update 구문
     */
    override suspend fun likePessimisticLock1(postId: Long, memberId: Long) = coroutineScope {
        transaction {
            val postLike = postLikePersistencePort.save(PostLike.from(Snowflake.nextId(), postId, memberId))

            launch {
                postLikeCountPersistencePort.increase(postId).takeIf { it == 0 }?.let {
                    /***
                     * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
                     * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
                     */
                    /***
                     * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
                     * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
                     */
                    /***
                     * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
                     * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
                     */

                    /***
                     * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
                     * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
                     */
                    postLikeCountPersistencePort.save(PostLikeCount(postId, 1L))
                }
            }

            postEventDispatcherPort.dispatch(
                type = EventType.POST_LIKED,
                payload = PostLikedEventPayload.from(postLike, count(postLike.postId))
            )
        }
    }

    override suspend fun unlikePessimisticLock1(postId: Long, memberId: Long): Unit = coroutineScope {
        transaction {
            postLikePersistencePort.findByPostIdAndMemberId(postId, memberId)
                ?.let { postLike ->
                    launch { postLikePersistencePort.delete(postLike) }
                    launch { postLikeCountPersistencePort.decrease(postId) }

                    postEventDispatcherPort.dispatch(
                        type = EventType.POST_UNLIKED,
                        payload = PostUnlikedEventPayload.from(postLike, count(postLike.postId))
                    )
                }
        }
    }

    /**
     * select ... for update + update
     */
    override suspend fun likePessimisticLock2(postId: Long, memberId: Long) = coroutineScope {
        transaction {
            launch { postLikePersistencePort.save(PostLike.from(Snowflake.nextId(), postId, memberId)) }

            val postLikeCount = postLikeCountPersistencePort.findLockedByPostId(postId)
                ?: PostLikeCount(postId, 0L)
            postLikeCount.increase()
            postLikeCountPersistencePort.save(postLikeCount) // find가 안된 경우 새로 생성하기 때문에, save 명시적 호출
        }
    }

    override suspend fun unlikePessimisticLock2(postId: Long, memberId: Long) = coroutineScope {
        transaction {
            postLikePersistencePort.findByPostIdAndMemberId(postId, memberId)
                ?.let { postLike ->
                    launch { postLikePersistencePort.delete(postLike) }
                    val postLikeCount = postLikeCountPersistencePort.findLockedByPostId(postId)
                        ?: throw RuntimeException("count not found")
                    postLikeCount.decrease()
                    postLikeCountPersistencePort.save(postLikeCount) // does not need in JPA
                }
        }
    }

    override suspend fun likeOptimisticLock(postId: Long, memberId: Long) = coroutineScope {
        transaction {
            launch { postLikePersistencePort.save(PostLike.from(Snowflake.nextId(), postId, memberId)) }

            val postLikeCount = postLikeCountPersistencePort.findById(postId)
                ?: PostLikeCount(postId, 0L)
            postLikeCount.increase()
            postLikeCountPersistencePort.save(postLikeCount)
        }
    }

    override suspend fun unlikeOptimisticLock(postId: Long, memberId: Long) = coroutineScope {
        transaction {
            postLikePersistencePort.findByPostIdAndMemberId(postId, memberId)
                ?.let { postLike ->
                    launch { postLikePersistencePort.delete(postLike) }
                    val postLikeCount = postLikeCountPersistencePort.findById(postId)
                        ?: throw RuntimeException("count not found")
                    postLikeCount.decrease()
                    postLikeCountPersistencePort.save(postLikeCount)
                }
        }
    }

    override suspend fun count(postId: Long): Long {
        return postLikeCountPersistencePort.findById(postId)?.likeCount ?: 0
    }
}