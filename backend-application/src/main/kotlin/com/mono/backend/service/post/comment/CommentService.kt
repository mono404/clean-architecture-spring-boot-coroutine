package com.mono.backend.service.post.comment

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.common.util.PageLimitCalculator
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.CommentCreatedEventPayload
import com.mono.backend.domain.event.payload.CommentDeletedEventPayload
import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.domain.post.comment.PostCommentCount
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePort
import com.mono.backend.port.infra.comment.persistence.PostCommentCountPersistencePort
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.post.comment.CommentUseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequest
import com.mono.backend.port.web.post.comment.dto.CommentPageResponse
import com.mono.backend.port.web.post.comment.dto.CommentResponse
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentPersistencePort: CommentPersistencePort,
    private val postCommentCountPersistencePort: PostCommentCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort,
    private val memberUseCase: MemberUseCase,
) : CommentUseCase {
    override suspend fun create(memberId: Long, postId: Long, request: CommentCreateRequest): CommentResponse =
        coroutineScope {
            transaction {
                launch {
                    postCommentCountPersistencePort.increase(postId).takeIf { it == 0 }?.let {
                        postCommentCountPersistencePort.save(PostCommentCount(postId, 1L))
                    }
                }

                val parent = async { findParent(request) }
                val member = async { memberUseCase.getEmbeddedMember(memberId) }

                val comment = commentPersistencePort.save(
                    Comment.create(
                        commentId = Snowflake.nextId(),
                        content = request.content,
                        parentCommentId = parent.await()?.parentCommentId,
                        postId = postId,
                        member = member.await()
                    )
                )

                postEventDispatcherPort.dispatch(
                    type = EventType.POST_COMMENT_CREATED,
                    payload = CommentCreatedEventPayload.from(comment, count(comment.postId)),
                )

                CommentResponse.from(comment)
            }
        }

    private suspend fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId?.toLong() ?: return null
        return commentPersistencePort
            .findById(parentCommentId)
            ?.takeIf { !it.deleted && it.isRoot() }
    }

    override suspend fun read(commentId: Long): CommentResponse? {
        return commentPersistencePort.findById(commentId)?.let { CommentResponse.from(it) }
    }

    override suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponse {
        val comment = commentPersistencePort.findById(commentId) ?: throw RuntimeException("Comment not found")
        val updatedComment = comment.copy(content = request.content)
        commentPersistencePort.save(updatedComment)
        return CommentResponse.from(updatedComment)
    }

    override suspend fun delete(commentId: Long) {
        transaction {
            commentPersistencePort.findById(commentId)
                ?.takeUnless { it.deleted }
                ?.let { comment ->
                    if (hasChildren(comment)) {
                        comment.delete()
                        commentPersistencePort.save(comment) // does not need in JPA
                    } else {
                        delete(comment)
                    }

                    postEventDispatcherPort.dispatch(
                        type = EventType.POST_COMMENT_DELETED,
                        payload = CommentDeletedEventPayload.from(comment, count(comment.postId)),
                    )
                }
        }
    }

    private suspend fun hasChildren(comment: Comment): Boolean {
        return commentPersistencePort.countBy(comment.postId, comment.commentId, 2L) == 2L
    }

    private suspend fun delete(comment: Comment): Unit = coroutineScope {
        launch { commentPersistencePort.delete(comment) }
        launch { postCommentCountPersistencePort.decrease(comment.postId) }
        launch {
            if (!comment.isRoot()) {
                commentPersistencePort.findById(comment.parentCommentId)
                    ?.takeIf { it.deleted && !hasChildren(it) }
                    ?.let { delete(it) }
            }
        }
    }

    override suspend fun readAll(postId: Long, pageRequest: PageRequest): CommentPageResponse = coroutineScope {
        val comment = async {
            commentPersistencePort.findAll(postId, pageRequest)
                .map(CommentResponse::from)
        }
        val commentCount = async {
            commentPersistencePort.count(postId, PageLimitCalculator.calculatePageLimit(pageRequest, 10L))
        }
        CommentPageResponse(
            comment.await(),
            commentCount.await()
        )
    }

    override suspend fun readAllInfiniteScroll(
        postId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        limit: Long
    ): List<CommentResponse> {
        val comments = if (lastParentCommentId == null || lastCommentId == null)
            commentPersistencePort.findAllInfiniteScroll(postId, limit)
        else
            commentPersistencePort.findAllInfiniteScroll(postId, lastParentCommentId, lastCommentId, limit)

        return comments.map(CommentResponse::from)
    }

    override suspend fun count(postId: Long): Long {
        return postCommentCountPersistencePort.findById(postId)?.commentCount ?: 0L
    }

    override suspend fun countAll(postIds: List<Long>): Map<Long, Long> {
        return postCommentCountPersistencePort.findByIds(postIds).associate { it.postId to it.commentCount }
    }
}