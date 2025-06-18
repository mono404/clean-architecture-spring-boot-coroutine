package com.mono.backend.s3client.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws.s3")
data class UploadConfig(
    val multipartThreshold: Long = 20 * 1024 * 1024, // 20MB 기준 자동 전환
    val multipartPartSize: Long = 5 * 1024 * 1024, // 5MB 기준 분할 업로드
    val maxParallelUploads: Int = 4,
    val maxRetries: Int = 3,
    val resumeEnabled: Boolean = true,
)
