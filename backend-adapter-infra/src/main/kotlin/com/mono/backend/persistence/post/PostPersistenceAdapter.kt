package com.mono.backend.persistence.post

import com.mono.backend.post.Post
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val postRepository: PostRepository,
): PostPersistencePort {
    override suspend fun save(post: Post): Post {
        return postRepository.save(PostEntity.from(post)).toDomain()
    }

    override suspend fun findById(postId: Long): Post? {
        return postRepository.findById(postId)?.toDomain()
    }

    override suspend fun findAll(boardId: Long, offset: Long, limit: Long): List<Post> {
        return postRepository.findAll(boardId, offset, limit).map(PostEntity::toDomain)
    }

    override suspend fun count(boardId: Long, limit: Long): Long {
        return postRepository.count(boardId, limit)
    }

    override suspend fun findAllInfiniteScroll(limit: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(limit).map(PostEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(boardId: Long, limit: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(boardId, limit).map(PostEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(boardId: Long, limit: Long, lastPostId: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(boardId, limit, lastPostId).map(PostEntity::toDomain)
    }

    override suspend fun delete(post: Post) {
        return postRepository.delete(PostEntity.from(post))
    }
}