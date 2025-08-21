package com.mono.backend.infra.event.handler

import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.MemberUpdatedEventPayload
import com.mono.backend.infra.persistence.post.PostRepository
import com.mono.backend.infra.persistence.post.comment.CommentRepository
import com.mono.backend.infra.persistence.post.comment.CommentRepositoryV2
import com.mono.backend.port.infra.search.persistence.SearchPersistencePort
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import org.springframework.stereotype.Component

@Component
class MemberUpdatedEventHandler(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val commentRepositoryV2: CommentRepositoryV2,
    private val searchPersistencePort: SearchPersistencePort,
) : EventHandler<MemberUpdatedEventPayload> {
    private val log = logger()

    override suspend fun handlePostRead(event: Event<MemberUpdatedEventPayload>) = supervisorScope {
        val p = event.payload ?: return@supervisorScope
        throttle()
        postRepository.updateMemberFields(p.memberId, p.nickname, p.profileImageUrl)
        throttle()
        commentRepository.updateMemberFields(p.memberId, p.nickname, p.profileImageUrl)
        throttle()
        commentRepositoryV2.updateMemberFields(p.memberId, p.nickname, p.profileImageUrl)
    }

    override suspend fun handleSearchIndex(event: Event<MemberUpdatedEventPayload>) {
        val p = event.payload ?: return
        // Batch update search index denormalized member fields
        throttle()
        runCatching {
            searchPersistencePort.updateMemberFields(p.memberId, p.nickname, p.profileImageUrl)
        }.onFailure { e -> log.error("Failed to update search index for memberId=${p.memberId}", e) }
    }

    override fun supports(event: Event<MemberUpdatedEventPayload>): Boolean {
        return EventType.MEMBER_UPDATED == event.type
    }

    override fun findPostId(event: Event<MemberUpdatedEventPayload>): Long? = null

    private suspend fun throttle() {
        delay(50) // simple micro-sleep to smooth spikes
    }
}