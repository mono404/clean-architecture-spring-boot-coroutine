package com.mono.backend.infra.persistence.event.dlq

import com.mono.backend.domain.event.DeadLetterQueue
import com.mono.backend.domain.event.EventType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("dead_letter_queue")
data class DeadLetterQueueEntity(
    @Id
    val deadLetterQueueId: Long? = null,
    val eventType: String,
    val aggregateId: String,
    val payloadJson: String,
    val lastError: String?,
    val attempts: Int,
    @CreatedDate
    val movedAt: LocalDateTime? = null,
) : Persistable<Long?> {
    override fun getId(): Long? = deadLetterQueueId
    override fun isNew(): Boolean = deadLetterQueueId == null

    fun toRecord(): DeadLetterQueue = DeadLetterQueue(
        deadLetterQueueId = deadLetterQueueId,
        eventType = EventType.valueOf(eventType),
        aggregateId = aggregateId,
        payloadJson = payloadJson,
        attempts = attempts,
        lastError = lastError,
        movedAt = movedAt,
    )
}