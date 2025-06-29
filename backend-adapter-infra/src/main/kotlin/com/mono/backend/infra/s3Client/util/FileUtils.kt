package com.mono.backend.infra.s3Client.util

import com.mono.backend.common.log.logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import java.nio.ByteBuffer

object FileUtils {
    private val log = logger()

    suspend fun FilePart.size(): Long {
        return content()
            .map { it.readableByteCount().toLong() }
            .reduce { acc, next -> acc + next }
            .awaitSingle()
    }

    fun DataBuffer.toByteArray(): ByteArray {
        val partSize = readableByteCount()
        val result = ByteArray(partSize)
        var offset = 0
        readableByteBuffers().use { buffers ->
            buffers.forEach { buffer ->
                val remaining = buffer.remaining()
                buffer.get(result, offset, remaining)
                offset += remaining
            }
        }
        return result
    }

    fun List<ByteArray>.combine(): ByteArray {
        val totalSize = sumOf { it.size }
        val combined = ByteArray(totalSize)
        var pos = 0
        for(b in this) {
            b.copyInto(combined, pos)
            pos += b.size
        }
        return combined
    }

    fun <T> Flow<T>.chunkedFlow(size: Int): Flow<List<T>> = flow {
        val buffer = mutableListOf<T>()
        collect { value ->
            buffer.add(value)
            if(buffer.size == size) {
                emit(buffer.toList())
                buffer.clear()
            }
        }
        if(buffer.isNotEmpty()) {
            emit(buffer.toList())
        }
    }

    fun List<DataBuffer>.toByteBuffer(): ByteBuffer {
        return dataBufferToByteBuffer(this)
    }

    fun dataBufferToByteBuffer(buffers: List<DataBuffer>): ByteBuffer {
        log.info("Creating ByteBuffer from {} chunks", buffers.size)

        var partSize = 0
        for (b in buffers) {
            partSize += b.readableByteCount()
        }

        val partData = ByteBuffer.allocate(partSize)
        buffers.forEach { buffer -> partData.put(buffer.toByteBuffer()) }

        partData.rewind()

        log.info("PartData: capacity={}", partData.capacity())
        return partData
    }


}