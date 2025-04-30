package com.mono.backend.persistence.common

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class TransactionExecutor(
    private val transactionManager: ReactiveTransactionManager
) : TransactionExecutorPort {
    override suspend fun <T> execute(
        readOnly: Boolean,
        propagation: Propagation,
        function: suspend () -> T
    ): T {
        val def = DefaultTransactionDefinition().apply {
            this.isReadOnly = readOnly
            this.propagationBehavior = toSpringPropagation(propagation)
        }

        val operator = TransactionalOperator.create(transactionManager, def)

        return operator.transactional(mono { function() }).awaitSingle()
    }

    private fun toSpringPropagation(propagation: Propagation) = when (propagation) {
        Propagation.REQUIRED -> TransactionDefinition.PROPAGATION_REQUIRED
        Propagation.REQUIRES_NEW -> TransactionDefinition.PROPAGATION_REQUIRES_NEW
        Propagation.MANDATORY -> TransactionDefinition.PROPAGATION_MANDATORY
        Propagation.NEVER -> TransactionDefinition.PROPAGATION_NEVER
        Propagation.NOT_SUPPORTED -> TransactionDefinition.PROPAGATION_NOT_SUPPORTED
        Propagation.NESTED -> TransactionDefinition.PROPAGATION_NESTED
        Propagation.SUPPORTS -> TransactionDefinition.PROPAGATION_SUPPORTS
    }
}