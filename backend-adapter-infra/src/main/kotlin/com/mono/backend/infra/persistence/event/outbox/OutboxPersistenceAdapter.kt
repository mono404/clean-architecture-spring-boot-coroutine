package com.mono.backend.infra.persistence.event.outbox

import com.mono.backend.common.snowflake.Snowflake
import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.OutboxMessage
import com.mono.backend.infra.dataserializer.DataSerializer
import com.mono.backend.infra.persistence.event.dlq.DeadLetterQueueRepository
import com.mono.backend.port.infra.event.OutboxPort
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
class OutboxPersistenceAdapter(
    private val outboxRepository: OutboxRepository,
    private val deadLetterQueueRepository: DeadLetterQueueRepository,
) : OutboxPort {

    override suspend fun enqueue(eventType: EventType, aggregateId: String, payload: EventPayload) {
        val entity = OutboxEntity(
            outboxId = Snowflake.nextId(),
            eventType = eventType.name,
            aggregateId = aggregateId,
            payloadJson = DataSerializer.serialize(payload) ?: "{}",
            status = OutboxStatus.PENDING,
            attempts = 0,
            nextAttemptAt = null,
            lastError = null,
        )
        outboxRepository.save(entity)
    }

    override suspend fun fetchPendingBatch(batchSize: Int): List<OutboxMessage> {
        return outboxRepository.fetchPending(batchSize).map {
            OutboxMessage(
                outBoxId = it.outboxId,
                eventType = EventType.valueOf(it.eventType),
                aggregateId = it.aggregateId,
                payloadJson = it.payloadJson,
                attempts = it.attempts,
            )
        }
    }

    override suspend fun markProcessing(id: Long, processingDeadline: Instant): Boolean {
        val updated = outboxRepository.markProcessing(
            id,
            LocalDateTime.ofInstant(processingDeadline, ZoneId.systemDefault())
        )
        return updated == 1
    }

    override suspend fun markDone(id: Long) {
        outboxRepository.markDone(id)
    }

    override suspend fun reschedule(id: Long, attempts: Int, nextAttemptAt: Instant, lastError: String?) {
        outboxRepository.reschedule(
            id,
            LocalDateTime.ofInstant(nextAttemptAt, ZoneId.systemDefault()),
            lastError
        )
        // Optionally store lastError in a separate log table
    }

    override suspend fun moveToDlq(message: OutboxMessage, lastError: String?) {
        deadLetterQueueRepository.upsertDlq(
            deadLetterQueueId = message.outBoxId,
            eventType = message.eventType.name,
            aggregateId = message.aggregateId,
            payloadJson = message.payloadJson,
            attempts = message.attempts,
            lastError = lastError
        )
    }

    override suspend fun recoverStaleProcessing(): Int {
        return outboxRepository.recoverStale(limit = 500)
    }
}