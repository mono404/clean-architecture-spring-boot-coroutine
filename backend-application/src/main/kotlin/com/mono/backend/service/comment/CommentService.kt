package com.mono.backend.service.comment

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.common.util.PageLimitCalculator
import com.mono.backend.domain.post.comment.Comment
import com.mono.backend.port.infra.comment.persistence.CommentPersistencePort
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.web.post.comment.CommentUseCase
import com.mono.backend.port.web.post.comment.dto.CommentCreateRequest
import com.mono.backend.port.web.post.comment.dto.CommentPageResponse
import com.mono.backend.port.web.post.comment.dto.CommentResponse
import com.mono.backend.port.web.post.comment.dto.CommentUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentPersistencePort: CommentPersistencePort
) : CommentUseCase {
    override suspend fun create(request: CommentCreateRequest): CommentResponse =
        transaction {
            val parent = findParent(request)
            val comment = commentPersistencePort.save(request.toDomain(Snowflake.nextId(), parent))
            CommentResponse.from(comment)
        }

    private suspend fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId ?: return null
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
                }
        }
    }

    private suspend fun hasChildren(comment: Comment): Boolean {
        return commentPersistencePort.countBy(comment.postId, comment.commentId, 2L) == 2L
    }

    private suspend fun delete(comment: Comment) {
        commentPersistencePort.delete(comment)
        if (!comment.isRoot()) {
            commentPersistencePort.findById(comment.parentCommentId)
                ?.takeIf { it.deleted && !hasChildren(it) }
                ?.let { delete(it) }
        }
    }

    override suspend fun readAll(postId: Long, page: Long, pageSize: Long): CommentPageResponse = coroutineScope {
        val comment = async {
            commentPersistencePort.findAll(postId, (page - 1) * pageSize, pageSize)
                .map(CommentResponse::from)
        }
        val commentCount = async {
            commentPersistencePort.count(postId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
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
}