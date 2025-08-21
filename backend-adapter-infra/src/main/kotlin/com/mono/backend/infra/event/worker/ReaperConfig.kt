package com.mono.backend.infra.event.worker

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "reaper")
data class ReaperConfig(
    val enabled: Boolean = true,
    val fixedDelay: Duration = Duration.ofSeconds(60),
    val phase: Int = 101
)