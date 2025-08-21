package com.mono.backend.infra.event

import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.payload.*
import com.mono.backend.port.infra.event.EventDispatcherPort
import com.mono.backend.port.infra.event.OutboxPort
import org.springframework.stereotype.Component

@Component
class EventDispatcherAdapter(
    private val outboxPort: OutboxPort
) : EventDispatcherPort {
    private val log = logger()

    override suspend fun dispatch(type: EventType, payload: EventPayload) {
        val aggregateId = resolveAggregateId(type, payload)
        outboxPort.enqueue(type, aggregateId, payload)
        log.info("[PostEventDispatcher] enqueued type={} aggregateId={}", type, aggregateId)
    }

    private fun resolveAggregateId(type: EventType, payload: EventPayload): String = when (type) {
        EventType.POST_CREATED -> (payload as PostCreatedEventPayload).postId.toString()
        EventType.POST_UPDATED -> (payload as PostUpdatedEventPayload).postId.toString()
        EventType.POST_DELETED -> (payload as PostDeletedEventPayload).postId.toString()
        EventType.POST_LIKED -> (payload as PostLikedEventPayload).postId.toString()
        EventType.POST_UNLIKED -> (payload as PostUnlikedEventPayload).postId.toString()
        EventType.POST_VIEWED -> (payload as PostViewedEventPayload).postId.toString()
        EventType.POST_COMMENT_CREATED -> (payload as CommentCreatedEventPayload).postId.toString()
        EventType.POST_COMMENT_UPDATED -> (payload as CommentUpdatedEventPayload).postId.toString()
        EventType.POST_COMMENT_DELETED -> (payload as CommentDeletedEventPayload).postId.toString()
        EventType.MEMBER_UPDATED -> (payload as MemberUpdatedEventPayload).memberId.toString()
    }
}