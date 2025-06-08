package com.mono.backend.event

import com.mono.backend.event.payload.*

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String
) {
    POST_CREATED(PostCreatedEventPayload::class.java, Topic.KUKE_BOARD_POST),
    POST_UPDATED(PostUpdatedEventPayload::class.java, Topic.KUKE_BOARD_POST),
    POST_DELETED(PostDeletedEventPayload::class.java, Topic.KUKE_BOARD_POST),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    POST_LIKED(PostLikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    POST_UNLIKED(PostUnlikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    POST_VIEWED(PostViewedEventPayload::class.java, Topic.KUKE_BOARD_VIEW);

    object Topic {
        const val KUKE_BOARD_POST = "kuke-board-post"
        const val KUKE_BOARD_COMMENT = "kuke-board-comment"
        const val KUKE_BOARD_LIKE = "kuke-board-like"
        const val KUKE_BOARD_VIEW = "kuke-board-view"
    }
}