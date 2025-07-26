package com.mono.backend.common.util

import org.slf4j.Logger

object CommonUtils {
    fun String.convertToBeanName(): String {
        val tokens = this.split("_", "-", " ")

        val capitalizing: String =
            tokens
                .drop(1)
                .joinToString("") { word ->
                    word.replaceFirstChar { char ->
                        char.uppercaseChar()
                    }
                }

        return tokens.first() + capitalizing
    }

    inline fun <T> T.applyWhen(
        predicate: Boolean,
        block: T.() -> Unit,
    ): T {
        if (predicate) {
            this.apply(block)
        }
        return this
    }

    suspend inline fun <T> T.runCatchingAndLog(
        log: Logger,
        location: String,
        crossinline block: suspend () -> T,
    ): T? {
        return runCatching {
            block()
        }.onFailure { throwable ->
            log.error("[$location] Failed", throwable)
        }.getOrNull()
    }
}