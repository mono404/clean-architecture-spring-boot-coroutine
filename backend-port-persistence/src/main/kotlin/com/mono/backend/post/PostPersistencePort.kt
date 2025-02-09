package com.mono.backend.post

interface PostPersistencePort {
    suspend fun save(post: Post): Int
    fun findAll(): List<Post>
    fun findById(id: Int): Post
    fun update(id: Int, post: Post): Int
    fun deleteById(id: Int)
}