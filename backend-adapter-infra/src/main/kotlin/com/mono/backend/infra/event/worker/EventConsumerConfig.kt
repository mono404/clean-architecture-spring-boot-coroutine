package com.mono.backend.infra.event.worker

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "worker")
data class EventConsumerConfig(
    val enabled: Boolean = true,
    val batchSize: Int = 100,
    val visibilityTimeoutSeconds: Duration = Duration.ofMinutes(2),

    val queueCapacity: Int = 1024,
    val emptyBackoffInitial: Duration = Duration.ofMillis(200),
    val emptyBackoffMax: Duration = Duration.ofSeconds(5),
    val jitterPercent: Int = 20,

    val phase: Int = 100,
)