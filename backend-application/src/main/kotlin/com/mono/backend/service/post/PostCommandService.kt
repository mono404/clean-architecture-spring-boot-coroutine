package com.mono.backend.service.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.common.util.PageLimitCalculator
import com.mono.backend.domain.common.pagination.CursorRequest
import com.mono.backend.domain.common.pagination.PageRequest
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostCreatedEventPayload
import com.mono.backend.domain.event.payload.PostDeletedEventPayload
import com.mono.backend.domain.event.payload.PostUpdatedEventPayload
import com.mono.backend.domain.post.board.BoardPostCount
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import com.mono.backend.port.infra.post.persistence.BoardPostCountPersistencePort
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import com.mono.backend.port.infra.s3client.S3UploadClientPort
import com.mono.backend.port.web.post.PostCommandUseCase
import com.mono.backend.port.web.post.dto.PostCreateRequest
import com.mono.backend.port.web.post.dto.PostPageResponse
import com.mono.backend.port.web.post.dto.PostResponse
import com.mono.backend.port.web.post.dto.PostUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class PostCommandService(
    private val postPersistencePort: PostPersistencePort,
    private val boardPostCountPersistencePort: BoardPostCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort,
    private val s3UploadClientPort: S3UploadClientPort,
    private val objectMapper: ObjectMapper
) : PostCommandUseCase {
    override suspend fun create(
        request: PostCreateRequest,
        mediaFiles: List<FilePart>?
    ): PostResponse = coroutineScope {
        transaction {
            val blobIds = extractBlobIds(request.content)
            val uploadedUrlMap = uploadFilesAsync(mediaFiles!!, blobIds)
            val content = replaceBlobUrls(request.content, uploadedUrlMap)

            val postCreateRequest = request.copy(content = content)
            val postDeferred = async { postPersistencePort.save(postCreateRequest.toDomain(Snowflake.nextId())) }

            launch {
                boardPostCountPersistencePort.increase(request.boardType).takeIf { it == 0 }?.let {
                    boardPostCountPersistencePort.save(BoardPostCount(request.boardType, 1L))
                }
            }

            val post = postDeferred.await()

            postEventDispatcherPort.dispatch(
                type = EventType.POST_CREATED,
                payload = PostCreatedEventPayload.from(post, count(post.boardType))
            )
            PostResponse.from(post)
        }
    }

    private suspend fun uploadFilesAsync(
        mediaFiles: List<FilePart>,
        blobIds: List<String>
    ): Map<String, String> {
        require(mediaFiles.size == blobIds.size)

        return coroutineScope {
            mediaFiles.mapIndexed { index, filePart ->
                async {
                    val fileResponse =
                        s3UploadClientPort.upload(filePart.filename(), filePart) { uploaded, total ->
                            println("Uploaded $uploaded/$total bytes")
                        }
                    blobIds[index] to fileResponse.path
                }
            }.awaitAll().toMap()
        }
    }

    override suspend fun update(postId: Long, request: PostUpdateRequest): PostResponse =
        transaction {
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
                    launch { boardPostCountPersistencePort.decrease(post.boardType) }
                }

                postEventDispatcherPort.dispatch(
                    type = EventType.POST_DELETED,
                    payload = PostDeletedEventPayload.from(post, count(post.boardType))
                )
            }
        }
    }

    suspend fun readAll(boardType: BoardType, pageRequest: PageRequest): PostPageResponse = coroutineScope {
        val posts = async {
            postPersistencePort.findAll(boardType, pageRequest).map(PostResponse::from)
        }
        val postCount = async {
            postPersistencePort.count(boardType, PageLimitCalculator.calculatePageLimit(pageRequest, 10L))
        }
        PostPageResponse(
            posts.await(),
            postCount.await()
        )
    }

    suspend fun readAllInfiniteScroll(boardType: BoardType, cursorRequest: CursorRequest): List<PostResponse> {
        val posts = if (boardType == BoardType.ALL) {
            postPersistencePort.findAllInfiniteScroll(cursorRequest.size)
        } else {
            if (cursorRequest.cursor == null)
                postPersistencePort.findAllInfiniteScroll(boardType, cursorRequest.size)
            else
                postPersistencePort.findAllInfiniteScroll(boardType, cursorRequest.size, cursorRequest.cursor!!)
        }

        return posts.map(PostResponse::from)
    }

    override suspend fun count(boardType: BoardType): Long {
        return boardPostCountPersistencePort.findById(boardType)?.postCount ?: 0
    }

    fun replaceBlobUrls(deltaJson: String, uploadedUrlMap: Map<String, String>): String {
        val nodes = objectMapper.readTree(deltaJson)

        for (node in nodes) {
            val insertNode = node["insert"]
            listOf("image", "video").forEach { mediaType ->
                if (insertNode?.has(mediaType) == true) {
                    val url = insertNode[mediaType].asText()
                    val uploadedUrl = uploadedUrlMap[url]
                    if (uploadedUrl != null) {
                        (insertNode as ObjectNode).put(mediaType, uploadedUrl)
                    }
                }
            }
        }
        return objectMapper.writeValueAsString(nodes)
    }

    fun extractBlobIds(deltaJson: String): List<String> {
        val nodes = objectMapper.readTree(deltaJson)

        val ids = mutableListOf<String>()

        for (node in nodes) {
            val insertNode = node["insert"]
            listOf("image", "video").forEach { mediaType ->
                if (insertNode?.has(mediaType) == true) {
                    val url = insertNode[mediaType].asText()
                    ids.add(url)
                }
            }
        }
        return ids
    }
}