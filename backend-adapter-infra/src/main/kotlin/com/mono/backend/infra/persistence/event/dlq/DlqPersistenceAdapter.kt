package com.mono.backend.infra.persistence.event.dlq

import com.mono.backend.domain.event.DeadLetterQueue
import com.mono.backend.port.infra.event.DlqPort
import org.springframework.stereotype.Repository

@Repository
class DlqPersistenceAdapter(
    private val deadLetterQueueRepository: DeadLetterQueueRepository
) : DlqPort {
    override suspend fun save(record: DeadLetterQueue) {
        deadLetterQueueRepository.upsertDlq(
            deadLetterQueueId = record.deadLetterQueueId!!,
            eventType = record.eventType.name,
            aggregateId = record.aggregateId,
            payloadJson = record.payloadJson,
            attempts = record.attempts,
            lastError = record.lastError
        )
    }

    override suspend fun delete(id: Long) {
        deadLetterQueueRepository.deleteById(id)
    }

    override suspend fun findBatch(limit: Int): List<DeadLetterQueue> {
        return deadLetterQueueRepository.findBatch(limit).map { it.toRecord() }
    }
}