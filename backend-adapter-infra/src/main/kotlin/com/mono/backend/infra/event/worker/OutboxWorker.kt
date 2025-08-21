package com.mono.backend.infra.event.worker

import com.mono.backend.common.coroutine.CoroutineUtils
import com.mono.backend.common.log.logger
import com.mono.backend.domain.event.*
import com.mono.backend.infra.dataserializer.DataSerializer
import com.mono.backend.infra.event.EventConsumer
import com.mono.backend.port.infra.event.OutboxPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext
import kotlin.math.abs

@Component
class OutboxWorker(
    private val outboxPort: OutboxPort,
    private val eventConsumer: EventConsumer,
    private val props: EventConsumerConfig,
) : SmartLifecycle {
    companion object {
        private const val MAX_ATTEMPTS = 10
    }

    private val log = logger()
    private val started = AtomicBoolean(false)
    private val scope = CoroutineUtils.eventDispatchScope

    private lateinit var queues: Array<Channel<OutboxMessage>>
    private var feederJob: Job? = null
    private var shardJob: List<Job> = emptyList()

    override fun isAutoStartup(): Boolean = props.enabled
    override fun getPhase(): Int = props.phase
    override fun isRunning(): Boolean = started.get()

    override fun start() {
        if (!props.enabled) {
            log.info("[OutboxWorker] disabled by property, skipping start.")
            return
        }
        if (!started.compareAndSet(false, true)) return

        val shards = Topic.entries.size

        log.info(
            "[OutboxWorker] starting (sharded). shards={}, batchSize={}, visibilityTimeout={}, backoffInitial={}, backoffMax={}",
            shards, props.batchSize, props.visibilityTimeoutSeconds, props.emptyBackoffInitial, props.emptyBackoffMax,
        )

        // 샤드 큐 생성
        queues = Array(shards) { Channel(capacity = props.queueCapacity) }

        // 샤드별 단일 루프(직렬 처리) 시작
        shardJob = List(shards) { shard ->
            scope.launch {
                log.info("[OutboxWorker] shard #{} loop started", Topic.entries[shard].name)
                for (msg in queues[shard]) {
                    processMessage(msg)
                }
                log.info("[OutboxWorker] shard #{} loop ended", Topic.entries[shard].name)
            }
        }

        feederJob = scope.launch {
            runFeederLoop(shards)
        }
    }

    override fun stop() {
        if (!started.compareAndSet(true, false)) return
        log.info("[OutboxWorker] stopping...")

        feederJob?.cancel()
        runBlocking { feederJob?.join() }

        if (this::queues.isInitialized) queues.forEach { it.close() }
        runBlocking { shardJob.forEach { it.join() } }

        log.info("[OutboxWorker] stopped")
    }

    // Feeder: 배치 -> 샤드 라우팅
    private suspend fun runFeederLoop(shards: Int) {
        var backoff = props.emptyBackoffInitial.toMillis().coerceAtLeast(50L)
        val backoffMax = props.emptyBackoffMax.toMillis().coerceAtLeast(backoff)

        while (started.get() && coroutineContext.isActive) {
            val now = Instant.now()
            log.trace("[OutboxWorker] poll tick... backoff={}ms, now={}", backoff, now)

            val batch: List<OutboxMessage> = runCatching {
                outboxPort.fetchPendingBatch(props.batchSize)
            }.onFailure { e ->
                log.error("[OutboxWorker] fetchPendingBatch failed", e)
            }.getOrElse { emptyList() }

            if (batch.isEmpty()) {
                delay(backoff)
                backoff = (backoff * 2).coerceAtMost(backoffMax)
                continue
            }

            backoff = props.emptyBackoffInitial.toMillis()
            log.info("[OutboxWorker] fetched ${batch.size} messages")

            val ordered = batch
                .groupBy { it.aggregateId }
                .flatMap { (_, messages) -> messages.sortedBy { it.outBoxId } }

            for (message in ordered) {
                val deadline = Instant.now().plus(props.visibilityTimeoutSeconds)
                val claimed = outboxPort.markProcessing(
                    message.outBoxId,
                    deadline
                )
                if (!claimed) continue

                val shard = shardOf(message.aggregateId, shards)
                queues[shard].send(message)
            }

            delay(5)
        }
    }

    private fun shardOf(key: String, shards: Int): Int {
        val h = abs(key.hashCode())
        return h % shards
    }

    private suspend fun processMessage(message: OutboxMessage) {
        log.info("[OutboxWorker] processing message id = ${message.outBoxId}, type = ${message.eventType}, agg = ${message.aggregateId}, attempts = ${message.attempts}")

        try {
            val payload = deserializePayload(message.eventType, message.payloadJson)
            dispatch(message.eventType, payload)

            outboxPort.markDone(message.outBoxId)
            log.info("[OutboxWorker] message processed successfully id = ${message.outBoxId}")
        } catch (e: Exception) {
            val attemptsNext = message.attempts + 1
            if (attemptsNext >= MAX_ATTEMPTS) {
                log.error("[OutboxWorker] move to DLQ id=${message.outBoxId}, attempts=$attemptsNext", e)
                outboxPort.moveToDlq(message.copy(attempts = attemptsNext), e.message)
            } else {
                val nextAttemptAt = Instant.now().plus(backoffFor(attemptsNext))
                log.warn(
                    "[OutboxWorker] reschedule id=${message.outBoxId} attempts=$attemptsNext, nextAttemptAt=$nextAttemptAt",
                    e
                )
                outboxPort.reschedule(message.outBoxId, attemptsNext, nextAttemptAt, e.message)
            }
        }
    }

    private fun backoffFor(attempts: Int): Duration = when (attempts) {
        1 -> Duration.ofMinutes(1)
        2 -> Duration.ofMinutes(5)
        3 -> Duration.ofMinutes(15)
        else -> Duration.ofHours(1)
    }

    private fun deserializePayload(type: EventType, payloadJson: String): EventPayload {
        val payloadClass = type.payloadClass
        return requireNotNull(DataSerializer.deserialize(payloadJson, payloadClass))
    }

    private suspend fun dispatch(type: EventType, payload: EventPayload) {
        // Route by topic group; currently we only have Post events bound to PostEventHandler
        eventConsumer.handleEvent(
            Event(
                eventId = null,
                type = type,
                payload = payload
            )
        )
    }
}