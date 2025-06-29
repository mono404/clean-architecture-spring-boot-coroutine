package com.mono.backend.port.infra.comment.persistence

import com.mono.backend.domain.post.comment.CommentV2

interface CommentPersistencePortV2 {
    suspend fun save(commentV2: CommentV2): CommentV2
    suspend fun findById(commentId: Long): CommentV2?
    suspend fun findByPath(path: String): CommentV2?
    suspend fun findDescendantsTopPath(postId: Long, pathPrefix: String): String?
    suspend fun delete(commentV2: CommentV2)
    suspend fun findAll(postId: Long, offset: Long, limit: Long): List<CommentV2>
    suspend fun count(postId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(postId: Long, limit: Long): List<CommentV2>
    suspend fun findAllInfiniteScroll(postId: Long, lastPath: String, limit: Long): List<CommentV2>
    suspend fun findAllByPostId(postId: Long): List<CommentV2>
}