package com.mono.backend.domain.event

import com.mono.backend.domain.event.payload.*

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String
) {
    POST_CREATED(PostCreatedEventPayload::class.java, Topic.BOARD_POST),
    POST_UPDATED(PostUpdatedEventPayload::class.java, Topic.BOARD_POST),
    POST_DELETED(PostDeletedEventPayload::class.java, Topic.BOARD_POST),
    POST_LIKED(PostLikedEventPayload::class.java, Topic.BOARD_LIKE),
    POST_UNLIKED(PostUnlikedEventPayload::class.java, Topic.BOARD_LIKE),
    POST_VIEWED(PostViewedEventPayload::class.java, Topic.BOARD_VIEW),

    POST_COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.POST_COMMENT),
    POST_COMMENT_UPDATED(CommentUpdatedEventPayload::class.java, Topic.POST_COMMENT),
    POST_COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.POST_COMMENT),

    MEMBER_UPDATED(MemberUpdatedEventPayload::class.java, Topic.MEMBER)
    ;


    object Topic {
        const val BOARD_POST = "board-post"
        const val BOARD_LIKE = "board-like"
        const val BOARD_VIEW = "board-view"

        const val POST_COMMENT = "board-comment"

        const val MEMBER = "member"
    }
}