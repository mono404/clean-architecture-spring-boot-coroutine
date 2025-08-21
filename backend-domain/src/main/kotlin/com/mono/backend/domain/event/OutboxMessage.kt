package com.mono.backend.domain.event

data class OutboxMessage(
    val outBoxId: Long,
    val eventType: EventType,
    val aggregateId: String,
    val payloadJson: String,
    val attempts: Int,
)
