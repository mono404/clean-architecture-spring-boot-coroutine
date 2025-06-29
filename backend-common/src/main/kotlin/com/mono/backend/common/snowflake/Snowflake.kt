package com.mono.backend.common.snowflake

import java.util.random.RandomGenerator

object Snowflake {
    private const val UNUSED_BITS = 1
    private const val EPOCH_BITS = 41
    private const val NODE_ID_BITS = 10
    private const val SEQUENCE_BITS = 12

    private const val MAX_NODE_ID = (1L shl NODE_ID_BITS) - 1
    private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

    private val nodeId = RandomGenerator.getDefault().nextLong(MAX_NODE_ID + 1)

    // UTC = 2024-01-01T00:00:00Z
    private const val START_TIME_MILLIE = 1704067200000L

    private var lastTimeMillis = START_TIME_MILLIE
    private var sequence: Long = 0


    @Synchronized
    fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis < lastTimeMillis) {
            throw IllegalStateException("Invalid Time")
        }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) and MAX_SEQUENCE
            if (sequence == 0L) {
                currentTimeMillis = waitNextMillis(currentTimeMillis)
            }
        } else {
            sequence = 0L
        }

        lastTimeMillis = currentTimeMillis

        return (currentTimeMillis - START_TIME_MILLIE shl NODE_ID_BITS + SEQUENCE_BITS) or
                (nodeId shl SEQUENCE_BITS) or sequence
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var currentTime = currentTimestamp
        while (currentTime <= lastTimeMillis) {
            currentTime = System.currentTimeMillis()
        }
        return currentTime
    }
}