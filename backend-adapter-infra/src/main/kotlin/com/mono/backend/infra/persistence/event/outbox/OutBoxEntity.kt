package com.mono.backend.infra.persistence.event.outbox

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("outbox")
data class OutboxEntity(
    @Id
    val outboxId: Long,
    val eventType: String,
    val aggregateId: String,
    val payloadJson: String,
    val status: String,
    val attempts: Int,
    val nextAttemptAt: LocalDateTime?,
    val lastError: String?,
    val processingDeadline: LocalDateTime? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    override fun getId(): Long = outboxId
    override fun isNew(): Boolean = createdAt == null
}

object OutboxStatus {
    const val PENDING = "PENDING"
    const val PROCESSING = "PROCESSING"
    const val DONE = "DONE"
    const val FAILED = "FAILED"
}