package com.mono.backend.s3client.uploader

import com.mono.backend.s3client.config.UploadConfig
import com.mono.backend.s3client.storage.UploadStorageProvider
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.BeforeEach
import software.amazon.awssdk.services.s3.S3AsyncClient

@OptIn(ExperimentalCoroutinesApi::class)
class SmartUploaderTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var s3Client: S3AsyncClient
    private lateinit var smartUploader: SmartUploader
    private lateinit var resumeStorage: UploadStorageProvider
    private val config = UploadConfig(
        multipartThreshold = 5 * 1024L,
        multipartPartSize = 2 * 1024L,
        maxParallelUploads = 2
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        s3Client = mockk(relaxed = true)

        smartUploader = SmartUploader(
            s3Client = s3Client,
            config = config,
            resumeStorage = resumeStorage,
            bucketName = "wonho-bucket",
            localEndpoint = "http://s3.localhost.localstack.cloud:4566"
        )
    }
}