package com.mono.backend.config

import com.mono.backend.persistence.common.TransactionExecutor
import com.mono.backend.transaction.initTransactionExecutor
import jakarta.annotation.PostConstruct

class TransactionConfig(
    private val transactionExecutor: TransactionExecutor
) {
    @PostConstruct
    fun init() {
        initTransactionExecutor(transactionExecutor)
    }
}