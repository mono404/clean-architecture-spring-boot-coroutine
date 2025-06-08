package com.mono.backend.persistence.comment

import com.mono.backend.comment.Comment

interface CommentPersistencePort {
    suspend fun save(comment: Comment): Comment
    suspend fun findById(commentId: Long): Comment?
    suspend fun countBy(postId: Long, parentCommentId: Long, limit: Long): Long
    suspend fun delete(comment: Comment)
    suspend fun findAll(postId: Long, offset: Long, limit: Long): List<Comment>
    suspend fun count(postId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(postId: Long, limit: Long): List<Comment>
    suspend fun findAllInfiniteScroll(
        postId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long
    ): List<Comment>
}