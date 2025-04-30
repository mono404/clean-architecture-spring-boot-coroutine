package com.mono.backend.persistence.comment

import com.mono.backend.comment.Comment

interface CommentPersistencePort {
    suspend fun save(comment: Comment): Comment
    suspend fun findById(commentId: Long): Comment?
    suspend fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long
    suspend fun delete(comment: Comment)
    suspend fun findAll(articleId: Long, offset: Long, limit: Long): List<Comment>
    suspend fun count(articleId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(articleId: Long, limit: Long): List<Comment>
    suspend fun findAllInfiniteScroll(
        articleId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long
    ): List<Comment>
}