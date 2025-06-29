package com.mono.backend.port.infra.common.persistence

interface TransactionExecutorPort {
    suspend fun <T> execute(
        readOnly: Boolean = false,
        propagation: Propagation = Propagation.REQUIRED,
        function: suspend () -> T
    ): T
}