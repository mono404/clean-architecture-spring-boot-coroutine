package com.mono.backend

import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.ByteBuffer

@Component
class S3FileStorage(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
) : FileStoragePort {
    override suspend fun store(path: String, file: FilePart): String {
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(path)
            .acl("public-read")
            .build()

        val publisher = file.content()
            .flatMapIterable { dataBuffer ->
                val buffers = mutableListOf<ByteBuffer>()

                dataBuffer.readableByteBuffers().use { iterator ->
                    while (iterator.hasNext()) {
                        buffers.add(iterator.next())
                    }
                }

                DataBufferUtils.release(dataBuffer)
                buffers
            }

        val asyncBody = AsyncRequestBody.fromPublisher(publisher)

        s3Client.putObject(request, asyncBody).await()

        return ""
    }

}