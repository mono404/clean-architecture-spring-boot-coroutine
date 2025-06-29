package com.mono.backend.infra.s3Client.retry

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


object RetryEngine {
    suspend inline fun <reified T> run(
        maxRetries: Int = 3,
        initialDelay: Duration = 300.milliseconds,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxRetries - 1) {
            try {
                return block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay *= 2
            }
        }
        return block()
    }
}