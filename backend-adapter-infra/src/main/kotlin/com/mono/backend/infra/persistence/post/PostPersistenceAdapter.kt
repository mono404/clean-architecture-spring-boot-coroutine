package com.mono.backend.infra.persistence.post

import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import kotlinx.coroutines.flow.toList
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
        return postRepository.findAll(boardType.id, offset, limit).map { it.toDomain() }
    }

    override suspend fun count(boardType: BoardType, limit: Long): Long {
        return postRepository.count(boardType.id, limit)
    }

    override suspend fun findAllInfiniteScroll(boardType: BoardType, cursorRequest: CursorRequest): List<Post> {
        return if (boardType == BoardType.ALL) {
            if (cursorRequest.cursor == null) {
                postRepository.findAllInfiniteScroll(cursorRequest.size)
            } else {
                postRepository.findAllInfiniteScroll(cursorRequest.size, cursorRequest.cursor!!.toLong())
            }
        } else {
            if (cursorRequest.cursor == null)
                postRepository.findAllInfiniteScrollByBoard(boardType.id, cursorRequest.size)
            else
                postRepository.findAllInfiniteScrollByBoard(
                    boardType.id,
                    cursorRequest.size,
                    cursorRequest.cursor!!.toLong()
                )
        }.map { it.toDomain() }
    }

    override suspend fun delete(post: Post) {
        return postRepository.deleteById(post.postId)
    }

    override suspend fun findAllByIds(postIds: List<Long>): List<Post> {
        return postRepository.findAllById(postIds).toList().map { it.toDomain() }
    }
}