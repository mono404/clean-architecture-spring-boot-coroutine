package com.mono.backend.post

import com.mono.backend.FileStoragePort
import com.mono.backend.event.EventType
import com.mono.backend.event.payload.PostCreatedEventPayload
import com.mono.backend.event.payload.PostDeletedEventPayload
import com.mono.backend.event.payload.PostUpdatedEventPayload
import com.mono.backend.event.post.PostEventDispatcherPort
import com.mono.backend.persistence.post.BoardPostCountPersistencePort
import com.mono.backend.persistence.post.PostPersistencePort
import com.mono.backend.post.request.PostCreateRequest
import com.mono.backend.post.request.PostUpdateRequest
import com.mono.backend.post.response.PostPageResponse
import com.mono.backend.post.response.PostResponse
import com.mono.backend.snowflake.Snowflake
import com.mono.backend.transaction.transaction
import com.mono.backend.util.PageLimitCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PostCommandService(
    private val postPersistencePort: PostPersistencePort,
    private val boardPostCountPersistencePort: BoardPostCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort,
    private val fileStoragePort: FileStoragePort
) : PostCommandUseCase {
    override suspend fun create(request: PostCreateRequest, mediaFiles: List<FilePart>?): PostResponse = coroutineScope {
        transaction {
            val postDeferred = async { postPersistencePort.save(request.toDomain(Snowflake.nextId())) }

            launch {
                boardPostCountPersistencePort.increase(request.boardId).takeIf { it == 0 }?.let {
                    boardPostCountPersistencePort.save(BoardPostCount(request.boardId, 1L))
                }
            }

            val uploadedUrls = mutableListOf<String>()
            if (mediaFiles != null) {
                for(filePart in mediaFiles) {
                    val originFilename = filePart.filename()
                    val extension = originFilename.substringAfterLast('.', "")
                    val uuid = UUID.randomUUID().toString()
                    val key = "posts/${LocalDateTime.now().year}/${uuid}.$extension"

                    val fileUrl = fileStoragePort.store(key, filePart)
                    uploadedUrls.add(fileUrl)
                }
            }

            val post = postDeferred.await()

            postEventDispatcherPort.dispatch(
                type = EventType.POST_CREATED,
                payload = PostCreatedEventPayload.from(post, count(post.boardId))
            )
            PostResponse.from(post)
        }
    }

    override suspend fun update(postId: Long, request: PostUpdateRequest): PostResponse = transaction {
        val post = postPersistencePort.findById(postId) ?: throw RuntimeException("Post not found")
        val updatedPost = post.copy(title = request.title, content = request.content)
        postPersistencePort.save(updatedPost) // does not need in JPA

        postEventDispatcherPort.dispatch(
            type = EventType.POST_UPDATED,
            payload = PostUpdatedEventPayload.from(post)
        )

        PostResponse.from(updatedPost)
    }

    suspend fun read(postId: Long) = postPersistencePort.findById(postId)?.let { PostResponse.from(it) }

    override suspend fun delete(postId: Long) {
        transaction {
            postPersistencePort.findById(postId)?.let { post ->
                coroutineScope {
                    launch { postPersistencePort.delete(post) }
                    launch { boardPostCountPersistencePort.decrease(post.boardId) }
                }

                postEventDispatcherPort.dispatch(
                    type = EventType.POST_DELETED,
                    payload = PostDeletedEventPayload.from(post, count(post.boardId))
                )
            }
        }
    }

    suspend fun readAll(boardId: Long, page: Long, pageSize: Long): PostPageResponse = coroutineScope {
        val posts = async {
            postPersistencePort.findAll(boardId, (page - 1) * pageSize, pageSize).map(PostResponse::from)
        }
        val postCount = async {
            postPersistencePort.count(boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        }
        PostPageResponse(
            posts.await(),
            postCount.await()
        )
    }

    suspend fun readAllInfiniteScroll(boardId: Long, lastPostId: Long?, pageSize: Long): List<PostResponse> {
        val posts = if(boardId == 0L) {
            postPersistencePort.findAllInfiniteScroll(pageSize)
        } else {
            if (lastPostId == null)
                postPersistencePort.findAllInfiniteScroll(boardId, pageSize)
            else
                postPersistencePort.findAllInfiniteScroll(boardId, pageSize, lastPostId)
        }

        return posts.map(PostResponse::from)
    }

    override suspend fun count(boardId: Long): Long {
        return boardPostCountPersistencePort.findById(boardId)?.postCount ?: 0
    }
}