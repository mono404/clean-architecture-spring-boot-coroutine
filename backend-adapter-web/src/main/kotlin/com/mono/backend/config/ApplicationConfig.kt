package com.mono.backend.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {
    private val applicationCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Bean
    fun applicationScope() = applicationCoroutineScope
}