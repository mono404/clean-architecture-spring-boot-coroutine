package com.mono.backend.persistence.comment

import com.mono.backend.comment.CommentV2

interface CommentPersistencePortV2 {
    suspend fun save(commentV2: CommentV2): CommentV2
    suspend fun findById(commentId: Long): CommentV2?
    suspend fun findByPath(path: String): CommentV2?
    suspend fun findDescendantsTopPath(articleId: Long, pathPrefix: String): String?
    suspend fun delete(commentV2: CommentV2)
    suspend fun findAll(articleId: Long, offset: Long, limit: Long): List<CommentV2>
    suspend fun count(articleId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(articleId: Long, limit: Long): List<CommentV2>
    suspend fun findAllInfiniteScroll(articleId: Long, lastPath: String, limit: Long): List<CommentV2>
}