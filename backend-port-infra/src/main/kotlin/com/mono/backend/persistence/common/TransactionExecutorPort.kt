package com.mono.backend.persistence.common

interface TransactionExecutorPort {
    suspend fun <T> execute(
        readOnly: Boolean = false,
        propagation: Propagation = Propagation.REQUIRED,
        function: suspend () -> T
    ): T
}