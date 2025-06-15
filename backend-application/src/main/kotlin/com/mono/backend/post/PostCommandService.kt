package com.mono.backend.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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
    private val fileStoragePort: FileStoragePort,
    private val objectMapper: ObjectMapper
) : PostCommandUseCase {
    override suspend fun create(
        request: PostCreateRequest,
        mediaFiles: List<FilePart>?,
        fileSizes: List<Long>?
    ): PostResponse = coroutineScope {
        transaction {
            val blobIds = extractBlobIds(request.content)
            val uploadedUrlMap = uploadFilesAsync(mediaFiles!!, fileSizes!!, blobIds)
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

    private suspend fun uploadFilesAsync(mediaFiles: List<FilePart>, fileSizes: List<Long>, blobIds: List<String>): Map<String, String> {
        require(mediaFiles.size == fileSizes.size && mediaFiles.size == blobIds.size)

        return coroutineScope {
            mediaFiles.mapIndexed { index, filePart ->
                val blobId = blobIds[index]
                val fileSize = fileSizes[index]

                async {
                    val uploadedUrl = fileStoragePort.store(blobId, filePart, fileSize)
                    blobId to uploadedUrl
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
                    if (url.startsWith("blob:")) {
                        val blobId = url.substringAfter("blob:")
                        val uploadedUrl = uploadedUrlMap[blobId]
                        if(uploadedUrl != null) {
                            (insertNode as ObjectNode).put(mediaType, uploadedUrl)
                        }
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
                    if (url.startsWith("blob:")) {
                        val blobId = url.substringAfter("blob:")
                        ids.add(blobId)
                    }
                }
            }
        }
        return ids
    }
}