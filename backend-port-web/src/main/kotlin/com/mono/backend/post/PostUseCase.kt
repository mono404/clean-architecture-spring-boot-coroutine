package com.mono.backend.post

interface PostUseCase {
    suspend fun create(post: Post): Int
    suspend fun findAll(): List<Post>
    suspend fun findById(id: Int): Post
    suspend fun update(id: Int, post: Post): Int
    suspend fun delete(id: Int)
}