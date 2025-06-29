package com.mono.backend.infra.persistence.post

import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val postRepository: PostRepository,
) : PostPersistencePort {
    override suspend fun save(post: Post): Post {
        return postRepository.save(PostEntity.from(post)).toDomain()
    }

    override suspend fun findById(postId: Long): Post? {
        return postRepository.findById(postId)?.toDomain()
    }

    override suspend fun findAll(boardType: BoardType, pageRequest: PageRequest): List<Post> {
        val offset = (pageRequest.page - 1) * pageRequest.size
        val limit = pageRequest.size
        return postRepository.findAll(boardType.id, offset, limit).map(PostEntity::toDomain)
    }

    override suspend fun count(boardType: BoardType, limit: Long): Long {
        return postRepository.count(boardType.id, limit)
    }

    override suspend fun findAllInfiniteScroll(limit: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(limit = limit).map(PostEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(boardType: BoardType, limit: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(boardId = boardType.id, limit = limit).map(PostEntity::toDomain)
    }

    override suspend fun findAllInfiniteScroll(boardType: BoardType, limit: Long, lastPostId: Long): List<Post> {
        return postRepository.findAllInfiniteScroll(boardId = boardType.id, limit = limit, lastPostId = lastPostId)
            .map(PostEntity::toDomain)
    }

    override suspend fun delete(post: Post) {
        return postRepository.delete(PostEntity.from(post))
    }
}