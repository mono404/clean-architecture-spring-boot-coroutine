package com.mono.backend.port.infra.s3client.model

import software.amazon.awssdk.services.s3.model.CompletedPart
import java.util.concurrent.atomic.AtomicInteger

data class UploadStatus(
    val fileKey: String,
    val contentType: String,
    var uploadId: String = "",
    var partCounter: AtomicInteger = AtomicInteger(0),
    var buffered: Int = 0,
    val completeParts: HashMap<Int, CompletedPart> = hashMapOf(),
    val uploadedSizes: HashMap<Int, Long> = hashMapOf()
) {
    fun addBuffered(buffered: Int) {
        this.buffered += buffered
    }

    fun getAddedPartCounter() = partCounter.incrementAndGet()
}
