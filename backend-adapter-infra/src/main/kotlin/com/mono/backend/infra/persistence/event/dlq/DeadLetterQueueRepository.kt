package com.mono.backend.infra.persistence.event.dlq

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeadLetterQueueRepository : CoroutineCrudRepository<DeadLetterQueueEntity, Long> {

    @Modifying
    @Query(
        """
            INSERT INTO dead_letter_queue (dead_letter_queue_id, event_type, aggregate_id, payload_json, attempts, last_error, moved_at)
            VALUES (:deadLetterQueueId, :eventType, :aggregateId, :payloadJson, :attempts, :lastError, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE 
                attempts = VALUES(attempts),
                last_error = VALUES(last_error),
                moved_at = VALUES(moved_at)
        """
    )
    suspend fun upsertDlq(
        deadLetterQueueId: Long,
        eventType: String,
        aggregateId: String,
        payloadJson: String,
        attempts: Int,
        lastError: String?
    ): Int

    @Query(
        """
        SELECT * FROM dead_letter_queue ORDER BY moved_at DESC, dead_letter_queue_id DESC LIMIT :limit
    """
    )
    suspend fun findBatch(limit: Int): List<DeadLetterQueueEntity>
}
