package com.mono.backend.s3client.uploader

import com.mono.backend.infra.s3Client.config.UploadConfig
import com.mono.backend.infra.s3Client.model.ProgressCallback
import com.mono.backend.infra.s3Client.storage.InMemoryStorageProvider
import com.mono.backend.infra.s3Client.uploader.S3Uploader
import com.mono.backend.infra.s3Client.util.AwsSdkUtils
import io.mockk.clearAllMocks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ListPartsRequest
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
class S3UploaderIntegrationTest {
    private lateinit var s3Client: S3AsyncClient
    private lateinit var s3Uploader: S3Uploader
    private lateinit var client: WebTestClient

    private val bucketName = "wonho-bucket"
    private val endpoint = "http://s3.localhost.localstack.cloud:4566"

    @BeforeEach
    fun setUp() {
        s3Client = S3AsyncClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
                )
            )
            .build()

//        runBlocking {
//            try {
//                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build()).join()
//            } catch (e: Exception) {
//                println("Bucket already exists")
//            }
//        }

        val config = UploadConfig(
            multipartThreshold = 5 * 1024 * 1024,    // 20MB 이상이면 멀티파트
            multipartPartSize = 5 * 1024 * 1024,      // 최소 허용 크기
            maxParallelUploads = 4
        )

        s3Uploader = S3Uploader(
            s3Client = s3Client,
            config = config,
            resumeStorage = InMemoryStorageProvider(),
            bucketName = bucketName,
            localEndpoint = endpoint
        )

        client = WebTestClient.bindToController(TestController(s3Uploader)).build()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `upload small file as single part`() = runTest {
        val content = ByteArray(100 * 1024) { 0x1 } // 256KB
        val fakeFilePart = FakeFilePart("small.jpg", content)

        val response = s3Uploader.upload("upload/single_file.png", fakeFilePart, null)
//        val response = smartUploader.upload("upload/single_file.png", fakeFilePart).awaitSingle()
        assertThat(response.uploadId).isEmpty()
        assertThat(response.eTag).isNotBlank()

        delete("upload/single_file.png") // Clean up after test
    }

    @Test
    fun `upload large file as multipart`() = runTest {
        val content = ByteArray(20_000_000) { 0x2 } // 2MB
        val fakeFilePart = FakeFilePart("large.mp4", content)

        val response = s3Uploader.upload("samples/large.mp4", fakeFilePart, null)
//        val response = smartUploader.uploadMultipart("upload/single_file.png", fakeFilePart).awaitSingle()

        assertThat(response.uploadId).isNotEmpty()

        val listRes = s3Client.listParts(
            ListPartsRequest.builder().bucket(bucketName).key("sample/large.mp4").uploadId(response.uploadId).build()
        ).join()
        assertThat(listRes.parts()).hasSizeGreaterThan(1)

        delete("sample/large.mp4") // Clean up after test
    }

    @Test
    fun `resume interrupted upload`() = runTest {
        val data = ByteArray(1_200_000) { 0x3 }
        val interrupted = FakeFilePart("resume.mov", data, interruptAfter = 1)

        // 1차 업로드 -> 중도 Exception
        runCatching { s3Uploader.upload("sample/resume.mov", interrupted, null) }

        // 2차 정산 업로드
        val response = s3Uploader.upload("sample/resume.mov", FakeFilePart("resume.mov", data), null)
        assertThat(response.eTag).isNotBlank()

        delete("sample/resume.mov") // Clean up after test
    }

    @Test
    fun `progress callback called`() = runTest {
        val data = ByteArray(900_000) { 0x4 }
        val counter = AtomicLong()
        val callback = object : ProgressCallback {
            override fun onProgress(uploadedBytes: Long, totalBytes: Long) {
                println("$uploadedBytes / $totalBytes")
                counter.set(uploadedBytes)
            }
        }
        s3Uploader.upload("sample/progress.bin", FakeFilePart("progress.bin", data), callback)
        assertThat(counter.get()).isEqualTo(900_000L)

        delete("sample/progress.bin") // Clean up after test
    }

    @Test
    fun `upload via webflux layer`() = runTest {
        val body = BodyInserters.fromMultipartData(
            "file",
            MockMultipartFile(
                "t1.png",
                "t1.png",
                MediaType.APPLICATION_OCTET_STREAM.toString(),
                ByteArray(123) { 0x5 },
            )
        )

        client.post()
            .uri("/upload/web")
            .body(body)
            .exchange()
            .expectStatus().isOk

        delete("upload/web/t1.png") // Clean up after test
    }

    suspend fun delete(fileKey: String) {
        val request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .build()
        s3Client.deleteObject(request).await()
            .also { AwsSdkUtils.checkSdkResponse(it) }
    }
}