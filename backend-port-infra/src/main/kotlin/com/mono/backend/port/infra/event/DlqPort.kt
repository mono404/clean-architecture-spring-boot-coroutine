package com.mono.backend.port.infra.event

import com.mono.backend.domain.event.DeadLetterQueue

/** Port for DLQ persistence and reprocessing. */
interface DlqPort {
    suspend fun save(record: DeadLetterQueue)
    suspend fun delete(id: Long)
    suspend fun findBatch(limit: Int): List<DeadLetterQueue>
}