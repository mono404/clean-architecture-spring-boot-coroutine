package com.mono.backend.infra.event

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.CommonUtils.runCatchingAndLog
import com.mono.backend.domain.event.Event
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.infra.event.handler.EventHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.springframework.stereotype.Component

@Component
class PostEventHandler(
    private val eventHandlers: List<EventHandler<*>>,
    private val hotPostScoreUpdater: HotPostScoreUpdater
) {
    private val log = logger()
    suspend fun handleEvent(event: Event<EventPayload>) = supervisorScope {
        val eventHandler = findEventHandler(event) as? EventHandler<EventPayload> ?: return@supervisorScope

        log.info("[PostEventHandler.handleEvent] handle event in eventHandler: ${eventHandler.javaClass.simpleName}")
        /** 게시글 캐시 업데이트 이벤트 */
        launch {
            runCatchingAndLog(log, "handlePostRead") { eventHandler.handlePostRead(event) }
        }
        /** 인기 게시글 캐시 업데이트 이벤트 */
        launch {
            runCatchingAndLog(log, "handleHotPost") {
                if (isPostCreatedOrDeleted(event)) {
                    eventHandler.handleHotPost(event)
                } else {
                    hotPostScoreUpdater.update(event, eventHandler)
                }
            }
        }
        /** Full Text Search 용 테이블 업데이트 */
        launch {
            runCatchingAndLog(log, "handleSearchIndex") { eventHandler.handleSearchIndex(event) }
        }
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<*>? {
        return eventHandlers.find { (it as EventHandler<EventPayload>).supports(event) }
    }

    private fun isPostCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return event.type == EventType.POST_CREATED || event.type == EventType.POST_DELETED
    }
}