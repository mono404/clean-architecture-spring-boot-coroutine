package com.mono.backend.config

import com.mono.backend.infra.persistence.common.TransactionExecutor
import com.mono.backend.port.infra.common.persistence.initTransactionExecutor
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class TransactionConfig(
    private val transactionExecutor: TransactionExecutor
) {
    @PostConstruct
    fun init() {
        initTransactionExecutor(transactionExecutor)
    }
}