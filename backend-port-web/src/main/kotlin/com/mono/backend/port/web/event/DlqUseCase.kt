package com.mono.backend.port.web.event

import com.mono.backend.domain.event.DeadLetterQueue

interface DlqUseCase {
    suspend fun list(limit: Int): List<DeadLetterQueue>
    suspend fun reprocess(id: Long)
}