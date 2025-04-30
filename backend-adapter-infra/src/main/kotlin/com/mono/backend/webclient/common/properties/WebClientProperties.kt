package com.mono.backend.webclient.common.properties

import reactor.netty.ReactorNetty
import java.time.Duration

private const val DEFAULT_CONNECTION_TIMEOUT: Int = 3000
private const val DEFAULT_READ_TIMEOUT: Long = 3000L
private const val DEFAULT_WRITE_TIMEOUT: Long = 9000L
private const val DEFAULT_MAX_RETRY: Long = 3

data class WebClientProperties(
    var name: String = "webClient",
    var url: String = "",
    var maxConnections: Int? = null,
    var connectionTimeout: Int = DEFAULT_CONNECTION_TIMEOUT,
    var readTimeout: Long = DEFAULT_READ_TIMEOUT,
    var writeTimeout: Long = DEFAULT_WRITE_TIMEOUT,
    var maxIdleTime: Duration? = null,
    var maxLifeTime: Duration? = null,
    var maxRetry: Long = DEFAULT_MAX_RETRY,
    var retryDelay: Duration = defaultRetryDelay,
) {
    fun maxConnection(): Int? =
        System
            .getProperty(ReactorNetty.POOL_MAX_CONNECTIONS, "")
            .toIntOrNull()
            ?: maxConnections

    @Suppress("ComplexMethod")
    fun overrideProperties(
        name: String,
        defaultProperties: WebClientProperties,
    ) {
        this.name = name
        if (maxConnections == null && defaultProperties.maxConnections != null) {
            maxConnections = defaultProperties.maxConnections
        }
        if (connectionTimeout == DEFAULT_CONNECTION_TIMEOUT &&
            defaultProperties.connectionTimeout != DEFAULT_CONNECTION_TIMEOUT
        ) {
            connectionTimeout = defaultProperties.connectionTimeout
        }
        if (readTimeout == DEFAULT_READ_TIMEOUT && defaultProperties.readTimeout != DEFAULT_READ_TIMEOUT) {
            readTimeout = defaultProperties.readTimeout
        }
        if (writeTimeout == DEFAULT_WRITE_TIMEOUT && defaultProperties.writeTimeout != DEFAULT_WRITE_TIMEOUT) {
            writeTimeout = defaultProperties.writeTimeout
        }
        if (maxIdleTime == null && defaultProperties.maxIdleTime != null) {
            maxIdleTime = defaultProperties.maxIdleTime
        }
        if (maxLifeTime == null && defaultProperties.maxLifeTime != null) {
            maxLifeTime = defaultProperties.maxLifeTime
        }
        if (maxRetry == DEFAULT_MAX_RETRY && defaultProperties.maxRetry != DEFAULT_MAX_RETRY) {
            maxRetry = defaultProperties.maxRetry
        }
        if (retryDelay == defaultRetryDelay && defaultProperties.retryDelay != defaultRetryDelay) {
            retryDelay = defaultProperties.retryDelay
        }
    }

    companion object {
        val defaultRetryDelay: Duration = Duration.ofSeconds(1)
    }
}