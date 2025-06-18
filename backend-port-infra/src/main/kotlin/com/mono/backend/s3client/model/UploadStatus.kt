package com.mono.backend.s3client.model

import software.amazon.awssdk.services.s3.model.CompletedPart

data class UploadStatus(
    val fileKey: String,
    val contentType: String,
    var uploadId: String = "",
    var partCounter: Int = 0,
    var buffered: Int = 0,
    val completeParts: HashMap<Int, CompletedPart> = hashMapOf()
) {
    fun addBuffered(buffered: Int) {
        this.buffered += buffered
    }

    fun getAddedPartCounter() = ++this.partCounter
}
