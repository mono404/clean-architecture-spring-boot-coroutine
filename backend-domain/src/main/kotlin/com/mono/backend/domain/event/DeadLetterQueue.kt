package com.mono.backend.domain.event

import java.time.LocalDateTime

data class DeadLetterQueue(
    val deadLetterQueueId: Long? = null,
    val eventType: EventType,
    val aggregateId: String,
    val payloadJson: String,
    val attempts: Int,
    val lastError: String?,
    val movedAt: LocalDateTime?
)
