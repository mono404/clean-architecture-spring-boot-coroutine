package com.mono.backend.s3client.uploader

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import java.nio.ByteBuffer
import java.nio.file.Path

class FakeFilePart(
    private val name: String,
    private val data: ByteArray,
    private val chunkSize: Int = 256 * 1024,
    private val interruptAfter: Int? = null
) : FilePart {
    override fun name(): String = name
    override fun headers(): HttpHeaders = HttpHeaders.EMPTY
    override fun filename(): String = name
    override fun transferTo(dest: Path) = throw UnsupportedOperationException()
    override fun content(): Flux<DataBuffer> {
        val factory = DefaultDataBufferFactory.sharedInstance
        val chunked = data.toList().chunked(chunkSize)
        return Flux.create { sink ->
            try {
                for ((index, chunk) in chunked.withIndex()) {
                    if (interruptAfter != null && index == interruptAfter) {
                        sink.error(RuntimeException("Simulated interrupt after $interruptAfter bytes"))
                        return@create
                    }
                    val byteBuffer = ByteBuffer.wrap(chunk.toByteArray())
                    val buffer = factory.wrap(byteBuffer)
                    sink.next(buffer)
                }
                sink.complete()
            } catch (e: Exception) {
                sink.error(e)
            }
        }
    }
}