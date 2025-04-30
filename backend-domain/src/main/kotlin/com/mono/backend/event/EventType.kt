package com.mono.backend.event

import com.mono.backend.event.payload.*

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.KUKE_BOARD_VIEW);

    object Topic {
        const val KUKE_BOARD_ARTICLE = "kuke-board-article"
        const val KUKE_BOARD_COMMENT = "kuke-board-comment"
        const val KUKE_BOARD_LIKE = "kuke-board-like"
        const val KUKE_BOARD_VIEW = "kuke-board-view"
    }
}