package com.mono.backend.infra.persistence.event.outbox

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OutboxRepository : CoroutineCrudRepository<OutboxEntity, Long> {
    @Query(
        """
        SELECT * FROM outbox
        WHERE status = 'PENDING' AND next_attempt_at <= CURRENT_TIMESTAMP
        ORDER BY next_attempt_at, outbox_id
        LIMIT :limit
    """
    )
    suspend fun fetchPending(limit: Int): List<OutboxEntity>

    @Modifying
    @Query(
        """
        UPDATE outbox
        SET status = 'PROCESSING', 
            processing_deadline = :deadline,
            updated_at = CURRENT_TIMESTAMP
        WHERE outbox_id = :id 
            AND status = 'PENDING'
            AND next_attempt_at <= CURRENT_TIMESTAMP
    """
    )
    suspend fun markProcessing(id: Long, deadline: LocalDateTime): Int

    @Modifying
    @Query(
        """
        UPDATE outbox
        SET status = 'DONE', updated_at = CURRENT_TIMESTAMP
        WHERE outbox_id = :id
            AND status = 'PROCESSING'
    """
    )
    suspend fun markDone(id: Long): Int

    @Modifying
    @Query(
        """
        UPDATE outbox
        SET status = 'PENDING', 
            attempts = attempts + 1, 
            next_attempt_at = :nextAttemptAt,
            last_error = :lastError,
            updated_at = CURRENT_TIMESTAMP
        WHERE outbox_id = :id
    """
    )
    suspend fun reschedule(id: Long, nextAttemptAt: LocalDateTime, lastError: String?): Int

    @Modifying
    @Query(
        """
        UPDATE outbox
        SET status = 'PENDING',
            attempts = attempts + 1,
            next_attempt_at = CURRENT_TIMESTAMP,
            processing_deadline = NULL, 
            updated_at = CURRENT_TIMESTAMP
        WHERE status = 'PROCESSING' 
            AND processing_deadline IS NOT NULL 
            AND processing_deadline < CURRENT_TIMESTAMP
        LIMIT :limit
    """
    )
    suspend fun recoverStale(limit: Int = 500): Int
}
