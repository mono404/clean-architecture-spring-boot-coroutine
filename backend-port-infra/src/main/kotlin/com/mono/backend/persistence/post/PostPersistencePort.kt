package com.mono.backend.persistence.post

import com.mono.backend.post.Post

interface PostPersistencePort {
    suspend fun save(post: Post): Post
    suspend fun findById(postId: Long): Post?
    suspend fun findAll(boardId: Long, offset: Long, limit: Long): List<Post>
    suspend fun count(boardId: Long, limit: Long): Long
    suspend fun findAllInfiniteScroll(limit: Long): List<Post>
    suspend fun findAllInfiniteScroll(boardId: Long, limit: Long): List<Post>
    suspend fun findAllInfiniteScroll(boardId: Long, limit: Long, lastPostId: Long): List<Post>
    suspend fun delete(post: Post)
}