package com.mono.backend.transaction

import com.mono.backend.persistence.common.Propagation
import com.mono.backend.persistence.common.TransactionExecutorPort

lateinit var transactionExecutorPort: TransactionExecutorPort

fun initTransactionExecutor(executor: TransactionExecutorPort) {
    transactionExecutorPort = executor
}

suspend inline fun <reified T> transaction(
    readOnly: Boolean = false,
    propagation: Propagation = Propagation.REQUIRED,
    noinline function: suspend () -> T
): T {
    return transactionExecutorPort.execute(readOnly, propagation, function)
}