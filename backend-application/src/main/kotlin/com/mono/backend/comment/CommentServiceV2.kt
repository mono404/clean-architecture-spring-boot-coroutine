package com.mono.backend.comment

import com.mono.backend.comment.request.CommentCreateRequestV2
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponseV2
import com.mono.backend.comment.response.CommentResponseV2
import com.mono.backend.event.EventType
import com.mono.backend.event.article.ArticleEventDispatcherPort
import com.mono.backend.event.payload.CommentCreatedEventPayload
import com.mono.backend.event.payload.CommentDeletedEventPayload
import com.mono.backend.persistence.comment.ArticleCommentCountPersistencePort
import com.mono.backend.persistence.comment.CommentPersistencePortV2
import com.mono.backend.snowflake.Snowflake
import com.mono.backend.transaction.transaction
import com.mono.backend.util.PageLimitCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class CommentServiceV2(
    private val commentPersistencePortV2: CommentPersistencePortV2,
    private val articleCommentCountPersistencePort: ArticleCommentCountPersistencePort,
    private val articleEventDispatcherPort: ArticleEventDispatcherPort
) : CommentV2UseCase {
    override suspend fun create(request: CommentCreateRequestV2): CommentResponseV2 = coroutineScope {
        transaction {
            val parent = findParent(request)
            val parentCommentPath = parent?.commentPath ?: CommentPath("").path
            val descendantsTopPath =
                commentPersistencePortV2.findDescendantsTopPath(request.articleId, parentCommentPath)
            val commentDeferred = async {
                commentPersistencePortV2.save(
                    request.toDomain(
                        Snowflake.nextId(),
                        parentCommentPath,
                        descendantsTopPath
                    )
                )
            }

            launch {
                articleCommentCountPersistencePort.increase(request.articleId).takeIf { it == 0 }?.let {
                    articleCommentCountPersistencePort.save(ArticleCommentCount(request.articleId, 1L))
                }
            }

            val comment = commentDeferred.await()
            articleEventDispatcherPort.dispatch(
                type = EventType.COMMENT_CREATED,
                payload = CommentCreatedEventPayload.from(comment, count(comment.articleId)),
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

    override suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2 = transaction {
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

                    articleEventDispatcherPort.dispatch(
                        type = EventType.COMMENT_DELETED,
                        payload = CommentDeletedEventPayload.from(comment, count(comment.articleId)),
                    )
                }
        }
    }

    private suspend fun hasChildren(comment: CommentV2): Boolean {
        return commentPersistencePortV2.findDescendantsTopPath(comment.articleId, comment.commentPath) != null
    }

    private suspend fun delete(comment: CommentV2): Unit = coroutineScope {
        launch { commentPersistencePortV2.delete(comment) }
        launch { articleCommentCountPersistencePort.decrease(comment.articleId) }
        launch {
            if (!comment.isRoot()) {
                commentPersistencePortV2.findByPath(CommentPath(comment.commentPath).getParentPath())
                    ?.takeIf { it.deleted && !hasChildren(it) }
                    ?.let { delete(it) }
            }
        }
    }

    override suspend fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponseV2 = coroutineScope {
        val comments = async {
            commentPersistencePortV2.findAll(articleId, (page - 1) * pageSize, pageSize)
                .map { CommentResponseV2.from(it) }
        }
        val commentCount = async {
            commentPersistencePortV2.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        }
        CommentPageResponseV2(
            comments.await(),
            commentCount.await()
        )
    }

    override suspend fun readAllInfiniteScroll(
        articleId: Long,
        lastPath: String?,
        pageSize: Long
    ): List<CommentResponseV2> {
        val comments = if (lastPath == null) {
            commentPersistencePortV2.findAllInfiniteScroll(articleId, pageSize)
        } else {
            commentPersistencePortV2.findAllInfiniteScroll(articleId, lastPath, pageSize)
        }

        return comments.map { CommentResponseV2.from(it) }
    }

    override suspend fun count(articleId: Long): Long {
        return articleCommentCountPersistencePort.findById(articleId)?.commentCount ?: 0L
    }
}