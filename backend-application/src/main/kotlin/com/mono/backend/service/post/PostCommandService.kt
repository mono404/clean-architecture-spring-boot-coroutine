package com.mono.backend.service.post

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.PostCreatedEventPayload
import com.mono.backend.domain.event.payload.PostDeletedEventPayload
import com.mono.backend.domain.event.payload.PostUpdatedEventPayload
import com.mono.backend.domain.mediafile.OwnerType
import com.mono.backend.domain.post.Post
import com.mono.backend.domain.post.board.BoardType
import com.mono.backend.port.infra.common.persistence.transaction
import com.mono.backend.port.infra.event.EventDispatcherPort
import com.mono.backend.port.infra.post.persistence.BoardPostCountPersistencePort
import com.mono.backend.port.infra.post.persistence.PostPersistencePort
import com.mono.backend.port.web.exceptions.NotFoundException
import com.mono.backend.port.web.mediafile.MediaFileUseCase
import com.mono.backend.port.web.member.MemberUseCase
import com.mono.backend.port.web.post.PostCommandUseCase
import com.mono.backend.port.web.post.dto.PostCreateOrUpdateRequest
import com.mono.backend.port.web.post.dto.PostResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class PostCommandService(
    private val postPersistencePort: PostPersistencePort,
    private val boardPostCountPersistencePort: BoardPostCountPersistencePort,
    private val eventDispatcherPort: EventDispatcherPort,
    private val memberUseCase: MemberUseCase,
    private val mediaFileUseCase: MediaFileUseCase,
) : PostCommandUseCase {
    override suspend fun create(
        memberId: Long,
        request: PostCreateOrUpdateRequest,
        mediaFiles: List<FilePart>
    ): PostResponse = coroutineScope {
        // 트랜잭션 내에서 핵심 비즈니스 로직만 수행
        val post = transaction {
            launch { boardPostCountPersistencePort.upsertIncrease(request.boardType) }

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
            val finalPost = if (newPost.containsImages() && mediaFiles.isNotEmpty()) {
                val uploadedUrlMap = mediaFileUseCase.uploadMediaFiles(newPost.postId, OwnerType.POST, mediaFiles)
                newPost.replaceImageUrls(uploadedUrlMap)
            } else {
                newPost
            }

            postPersistencePort.save(finalPost)
        }

        // 이벤트 발행 (fire-and-forget)
        launch {
            eventDispatcherPort.dispatch(
                type = EventType.POST_CREATED,
                payload = PostCreatedEventPayload.from(post, count(post.boardType))
            )
        }

        PostResponse.from(post)
    }

    override suspend fun update(
        memberId: Long,
        postId: Long,
        request: PostCreateOrUpdateRequest,
        mediaFiles: List<FilePart>
    ): PostResponse = coroutineScope {
        // 트랜잭션 내에서 핵심 업데이트만 수행
        val updatedPost = transaction {
            val post = postPersistencePort.findById(postId) ?: throw NotFoundException("Post not found")

            require(post.canBeEditedBy(memberId)) { "게시글 수정 권한이 없습니다." }

            // 도메인 비즈니스 로직으로 업데이트
            val updated = post.update(request.title, request.content)

            // 기존 이미지 삭제
            mediaFileUseCase.deleteMediaFiles(postId, OwnerType.POST)

            // 이미지가 있으면 처리
            val finalPost = if (updated.containsImages() && mediaFiles.isNotEmpty()) {
                val uploadedUrlMap = mediaFileUseCase.uploadMediaFiles(updated.postId, OwnerType.POST, mediaFiles)
                updated.replaceImageUrls(uploadedUrlMap)
            } else {
                updated
            }

            val savedPost = postPersistencePort.save(finalPost)

            savedPost
        }

        // 이벤트 발행 (fire-and-forget)
        launch {
            eventDispatcherPort.dispatch(
                type = EventType.POST_UPDATED,
                payload = PostUpdatedEventPayload.from(updatedPost)
            )
        }

        PostResponse.from(updatedPost)
    }

    override suspend fun delete(memberId: Long, postId: Long): Unit = coroutineScope {
        // 트랜잭션 내에서 삭제만 수행
        val deletedPost = transaction {
            postPersistencePort.findById(postId)?.also { post ->
                require(post.canBeDeletedBy(memberId)) { "게시글 삭제 권한이 없습니다." }
                launch { postPersistencePort.delete(post) }
                launch { boardPostCountPersistencePort.decrease(post.boardType) }
                launch { mediaFileUseCase.deleteMediaFiles(post.postId, OwnerType.POST) }
            }
        }

        deletedPost?.let { post ->
            // 이벤트 발행 (fire-and-forget)
            launch {
                eventDispatcherPort.dispatch(
                    type = EventType.POST_DELETED,
                    payload = PostDeletedEventPayload.from(post, count(post.boardType))
                )
            }
        }
    }

    suspend fun count(boardType: BoardType): Long = boardPostCountPersistencePort.findById(boardType)?.postCount ?: 0
}
