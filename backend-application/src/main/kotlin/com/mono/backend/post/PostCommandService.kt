package com.mono.backend.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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
import com.mono.backend.s3client.S3UploadClientPort
import com.mono.backend.snowflake.Snowflake
import com.mono.backend.transaction.transaction
import com.mono.backend.util.PageLimitCalculator
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
                boardPostCountPersistencePort.increase(request.boardId).takeIf { it == 0 }?.let {
                    boardPostCountPersistencePort.save(BoardPostCount(request.boardId, 1L))
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
        val posts = if (boardId == 0L) {
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

    fun extractBlobIdFromFilename(filename: String): String {
        return filename.substringBeforeLast(".").substringAfter("blob-")
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
                    val blobId = url.substringAfter("blob:")
                    ids.add(blobId)
                }
            }
        }
        return ids
    }
}