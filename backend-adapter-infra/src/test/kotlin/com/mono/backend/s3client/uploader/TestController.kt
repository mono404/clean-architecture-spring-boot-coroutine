package com.mono.backend.s3client.uploader

import com.mono.backend.infra.s3Client.uploader.S3Uploader
import kotlinx.coroutines.runBlocking
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.web.bind.annotation.PostMapping
import reactor.core.publisher.Mono

class TestController(
    private val uploader: S3Uploader
) {
    @PostMapping("/upload/web", consumes = ["multipart/form-data"])
    fun upload(fluxPart: Part): Mono<String> =
        if (fluxPart is FilePart) Mono.fromCallable {
            runBlocking {
                val response = uploader.upload("sample/web/${fluxPart.filename()}", fluxPart, null)
                response.path
            }
        }
        else Mono.error(IllegalArgumentException("not file"))
}