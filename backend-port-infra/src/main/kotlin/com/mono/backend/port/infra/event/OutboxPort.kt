package com.mono.backend.port.infra.event

import com.mono.backend.domain.event.EventPayload
import com.mono.backend.domain.event.EventType
import com.mono.backend.domain.event.OutboxMessage
import java.time.Instant


/**
 * Port for persisting and consuming outbox events.
 * This abstracts Outbox/DLQ storage for the worker and publishers.
 */
interface OutboxPort {
    /** Enqueue a new event for processing. Should be called within the business transaction. */
    suspend fun enqueue(eventType: EventType, aggregateId: String, payload: EventPayload)

    /** Fetch pending messages up to the given batch size. */
    suspend fun fetchPendingBatch(batchSize: Int): List<OutboxMessage>

    /** Mark the message as processing with a visibility timeout. Returns true if state transition succeeded. */
    suspend fun markProcessing(id: Long, processingDeadline: Instant): Boolean

    /** Mark the message as done. */
    suspend fun markDone(id: Long)

    /** Reschedule the message with backoff after failure. */
    suspend fun reschedule(id: Long, attempts: Int, nextAttemptAt: Instant, lastError: String?)

    /** Move the message to DLQ. */
    suspend fun moveToDlq(message: OutboxMessage, lastError: String?)

    /** Recover stuck messages whose processing deadline has passed. Returns number of recovered rows. */
    suspend fun recoverStaleProcessing(): Int
}