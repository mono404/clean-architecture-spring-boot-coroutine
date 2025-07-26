package com.mono.backend.service.post

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostCreatedEventPayload
import com.mono.backend.domain.event.payload.PostDeletedEventPayload
import com.mono.backend.domain.event.payload.PostUpdatedEventPayload
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.post.event.PostEventDispatcherPort
import com.mono.backend.port.infra.post.persistence.BoardPostCountPersistencePort
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import com.mono.backend.port.infra.s3client.S3UploadClientPort
import com.mono.backend.port.web.exceptions.NotFoundException
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.post.PostCommandUseCase
import com.mono.backend.port.web.post.dto.PostCreateRequest
import com.mono.backend.port.web.post.dto.PostResponse
import com.mono.backend.port.web.post.dto.PostUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PostCommandService(
    private val postPersistencePort: PostPersistencePort,
    private val boardPostCountPersistencePort: BoardPostCountPersistencePort,
    private val postEventDispatcherPort: PostEventDispatcherPort,
    private val s3UploadClientPort: S3UploadClientPort,
    private val memberUseCase: MemberUseCase
) : PostCommandUseCase {
    override suspend fun create(
        memberId: Long,
        request: PostCreateRequest,
        mediaFiles: List<FilePart>?
    ): PostResponse = coroutineScope {
        // 트랜잭션 내에서 핵심 비즈니스 로직만 수행
        val post = transaction {
            val member = memberUseCase.getEmbeddedMember(memberId)

            // 도메인 팩토리로 생성
            val newPost = Post.create(
                postId = Snowflake.nextId(),
                title = request.title,
                content = request.content,
                boardType = request.boardType,
                member = member
            )

            // 이미지가 있으면 처리
            val finalPost = if (newPost.containsImages() && !mediaFiles.isNullOrEmpty()) {
                val blobIds = newPost.extractImageBlobIds()
                val uploadedUrlMap = uploadFilesAsync(mediaFiles, blobIds)
                newPost.replaceImageUrls(uploadedUrlMap)
            } else {
                newPost
            }

            postPersistencePort.save(finalPost)
        }

        // 보드 카운트 증가 (별도 트랜잭션)
        launch {
            runCatching {
                transaction { boardPostCountPersistencePort.upsertIncrease(request.boardType) }
            }
        }

        // 이벤트 발행 (fire-and-forget)
        launch {
            runCatching {
                postEventDispatcherPort.dispatch(
                    type = EventType.POST_CREATED,
                    payload = PostCreatedEventPayload.from(post, count(post.boardType))
                )
            }
        }

        PostResponse.from(post)
    }

    override suspend fun update(postId: Long, request: PostUpdateRequest): PostResponse = coroutineScope {
        // 트랜잭션 내에서 핵심 업데이트만 수행
        val updatedPost = transaction {
            val post = postPersistencePort.findById(postId) ?: throw NotFoundException("Post not found")

//             TODO 권한 검사는 웹 계층에서 memberId를 받아서 처리해야 함
//             현재는 memberId 파라미터가 없으므로 임시로 주석 처리
//             require(post.canBeEditedBy(memberId)) { "게시글 수정 권한이 없습니다." }

            // 도메인 비즈니스 로직으로 업데이트
            val updated = post.update(request.title, request.content)
            postPersistencePort.save(updated)
            updated
        }

        // 이벤트 발행 (fire-and-forget)
        launch {
            runCatching {
                postEventDispatcherPort.dispatch(
                    type = EventType.POST_UPDATED,
                    payload = PostUpdatedEventPayload.from(updatedPost)
                )
            }
        }

        PostResponse.from(updatedPost)
    }

    override suspend fun delete(postId: Long): Unit = coroutineScope {
        // 트랜잭션 내에서 삭제만 수행
        val deletedPost = transaction {
            postPersistencePort.findById(postId)?.also { post ->
                // 권한 검사는 웹 계층에서 memberId를 받아서 처리해야 함
                // require(post.canBeDeletedBy(memberId)) { "게시글 삭제 권한이 없습니다." }
                postPersistencePort.delete(post)
            }
        }

        deletedPost?.let { post ->
            // 보드 카운트 감소 (별도 트랜잭션)
            launch {
                runCatching {
                    transaction { boardPostCountPersistencePort.decrease(post.boardType) }
                }
            }

            // 이벤트 발행 (fire-and-forget)
            launch {
                runCatching {
                    postEventDispatcherPort.dispatch(
                        type = EventType.POST_DELETED,
                        payload = PostDeletedEventPayload.from(post, count(post.boardType))
                    )
                }
            }
        }
    }

    suspend fun count(boardType: BoardType): Long = boardPostCountPersistencePort.findById(boardType)?.postCount ?: 0

    private suspend fun uploadFilesAsync(
        mediaFiles: List<FilePart>,
        blobIds: List<String>
    ): Map<String, String> {
        require(mediaFiles.size == blobIds.size)

        return coroutineScope {
            mediaFiles.mapIndexed { index, filePart ->
                async {
                    val fileName = "post_image/upload_${Instant.now().toEpochMilli()}.png"
                    val fileResponse = s3UploadClientPort.upload(fileName, filePart)
                    blobIds[index] to fileResponse.path
                }
            }.awaitAll().toMap()
        }
    }
}