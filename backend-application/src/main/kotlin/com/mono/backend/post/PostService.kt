package com.mono.backend.post

import org.springframework.stereotype.Service

@Service
class PostService(
    private val postPersistencePort: PostPersistencePort
): PostUseCase {
    override suspend fun create(post: Post): Int {
        return postPersistencePort.save(post)
    }

    override suspend fun findAll(): List<Post> {
        return postPersistencePort.findAll()
    }

    override suspend fun findById(id: Int): Post {
        return postPersistencePort.findById(id)
    }

    override suspend fun update(id: Int, post: Post): Int {
        return postPersistencePort.update(id, post)
    }

    override suspend fun delete(id: Int) {
        return postPersistencePort.deleteById(id)
    }
}