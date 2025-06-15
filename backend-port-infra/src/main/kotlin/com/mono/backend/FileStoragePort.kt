package com.mono.backend

import org.springframework.http.codec.multipart.FilePart

interface FileStoragePort {
    suspend fun store(path: String, file: FilePart, fileSize: Long): String
}