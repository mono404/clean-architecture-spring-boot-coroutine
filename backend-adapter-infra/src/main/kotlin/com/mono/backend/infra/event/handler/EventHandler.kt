package com.mono.backend.infra.event.handler

import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventPayload

interface EventHandler<T : EventPayload> {
    /** 인기 게시글 업데이트 이벤트 */
    suspend fun handleHotPost(event: Event<T>) = Unit

    /** 게시글 캐시 업데이트 이벤트 */
    suspend fun handlePostRead(event: Event<T>) = Unit

    /** Full Text Search 용 테이블 업데이트 */
    suspend fun handleSearchIndex(event: Event<T>) = Unit
    fun supports(event: Event<T>): Boolean
    fun findPostId(event: Event<T>): Long?
}