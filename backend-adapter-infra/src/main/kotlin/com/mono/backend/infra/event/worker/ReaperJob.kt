package com.mono.backend.infra.event.worker


import com.mono.backend.common.log.logger
import com.mono.backend.port.infra.event.OutboxPort
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "reaper", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class ReaperJob(
    private val outboxPort: OutboxPort,
    private val props: ReaperConfig,
) {
    private val log = logger()

    @Scheduled(fixedDelay = 60000)
    fun run() = runBlocking {
        val recovered = runCatching {
            outboxPort.recoverStaleProcessing()
        }.onFailure { e ->
            log.error("[ReaperJob] recover failed", e)
        }.getOrElse { 0 }

        log.trace("[ReaperJob] tick fixedDelay={}, recovered={}", props.fixedDelay, recovered)
    }
}
