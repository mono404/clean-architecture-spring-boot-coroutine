package com.mono.backend.common.util

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
}