package com.mono.backend.service.post.comment

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.common.util.PageLimitCalculator
import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.CommentCreatedEventPayload
import com.mono.backend.domain.event.payload.CommentDeletedEventPayload
import com.mono.backend.domain.post.comment.CommentPath
import com.mono.backend.domain.post.comment.CommentV2
import com.mono.backend.domain.post.comment.PostCommentCount
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePortV2
import com.mono.backend.port.infra.comment.persistence.PostCommentCountPersistencePort
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.post.comment.CommentV2UseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequestV2
import com.mono.backend.port.web.post.comment.dto.CommentPageResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentResponseV2
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class CommentServiceV2(
    private val commentPersistencePortV2: CommentPersistencePortV2,
    private val postCommentCountPersistencePort: PostCommentCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort,
    private val memberUseCase: MemberUseCase,
) : CommentV2UseCase {
    override suspend fun create(memberId: Long, postId: Long, request: CommentCreateRequestV2): CommentResponseV2 =
        coroutineScope {
            transaction {
                launch {
                    postCommentCountPersistencePort.increase(postId).takeIf { it == 0 }?.let {
                        postCommentCountPersistencePort.save(PostCommentCount(postId, 1L))
                    }
                }

                val comment = transaction {
                    val member = memberUseCase.getEmbeddedMember(memberId)
                    val parentCommentPath =
                        request.parentPath?.let { commentPersistencePortV2.findByPath(it)?.commentPath }
                            ?: CommentPath()
                    val descendantsTopPath =
                        commentPersistencePortV2.findDescendantsTopPath(postId, parentCommentPath.path)

                    commentPersistencePortV2.save(
                        CommentV2.create(
                            commentId = Snowflake.nextId(),
                            content = request.content,
                            postId = postId,
                            commentPath = parentCommentPath.createChildCommentPath(descendantsTopPath),
                            member = member
                        )
                    )
                }

                postEventDispatcherPort.dispatch(
                    type = EventType.POST_COMMENT_CREATED,
                    payload = CommentCreatedEventPayload.from(comment, count(comment.postId)),
                )

                CommentResponseV2.from(comment)
            }
        }

    private suspend fun findParent(request: CommentCreateRequestV2): CommentV2? {
        return request.parentPath?.let { path ->
            commentPersistencePortV2.findByPath(path)?.takeUnless { it.deleted }
        }
    }

    override suspend fun read(commentId: Long): CommentResponseV2? {
        return commentPersistencePortV2.findById(commentId)?.let { CommentResponseV2.from(it) }
    }

    override suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2 =
        transaction {
            val comment = commentPersistencePortV2.findById(commentId) ?: throw RuntimeException("Comment not found")
            val updatedComment = comment.copy(content = request.content)
            commentPersistencePortV2.save(updatedComment)
            CommentResponseV2.from(updatedComment)
        }


    override suspend fun delete(commentId: Long) {
        transaction {
            commentPersistencePortV2.findById(commentId)
                ?.takeIf { !it.deleted }
                ?.let { comment ->
                    if (hasChildren(comment)) {
                        comment.delete()
                        commentPersistencePortV2.save(comment)
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

    private suspend fun hasChildren(comment: CommentV2): Boolean {
        return commentPersistencePortV2.findDescendantsTopPath(comment.postId, comment.commentPath.path) != null
    }

    private suspend fun delete(comment: CommentV2): Unit = coroutineScope {
        launch { commentPersistencePortV2.delete(comment) }
        launch { postCommentCountPersistencePort.decrease(comment.postId) }
        launch {
            if (!comment.isRoot()) {
                commentPersistencePortV2.findByPath(comment.commentPath.getParentPath())
                    ?.takeIf { it.deleted && !hasChildren(it) }
                    ?.let { delete(it) }
            }
        }
    }

    override suspend fun readAll(postId: Long, pageRequest: PageRequest): CommentPageResponseV2 = coroutineScope {
        val comments = async {
            commentPersistencePortV2.findAll(postId, pageRequest)
                .map { CommentResponseV2.from(it) }
        }
        val commentCount = async {
            commentPersistencePortV2.count(postId, PageLimitCalculator.calculatePageLimit(pageRequest, 10L))
        }
        CommentPageResponseV2(
            comments.await(),
            commentCount.await()
        )
    }

    override suspend fun readAllInfiniteScroll(
        postId: Long,
        cursorRequest: CursorRequest,
    ): List<CommentResponseV2> {
        return commentPersistencePortV2.findAllInfiniteScroll(postId, cursorRequest).map { CommentResponseV2.from(it) }
    }

    override suspend fun count(postId: Long): Long {
        return postCommentCountPersistencePort.findById(postId)?.commentCount ?: 0L
    }

    override suspend fun countAll(postIds: List<Long>): Map<Long, Long> {
        return postCommentCountPersistencePort.findByIds(postIds).associate { it.postId to it.commentCount }
    }
}