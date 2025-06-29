package com.mono.backend.port.infra.post.persistence

import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType

interface PostPersistencePort {
    suspend fun save(post: Post): Post
    suspend fun findById(postId: Long): Post?
    suspend fun findAll(boardType: BoardType, pageRequest: PageRequest): List<Post>
    suspend fun count(boardType: BoardType, limit: Long): Long
    suspend fun findAllInfiniteScroll(limit: Long): List<Post>
    suspend fun findAllInfiniteScroll(boardType: BoardType, limit: Long): List<Post>
    suspend fun findAllInfiniteScroll(boardType: BoardType, limit: Long, lastPostId: Long): List<Post>
    suspend fun delete(post: Post)
}