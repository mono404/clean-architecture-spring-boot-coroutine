package com.mono.backend

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI
import java.time.Duration

@Configuration
class S3Config(
    @Value("\${aws.s3.region}") private val region: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
    @Value("\${aws.s3.numRetries}") private val numRetries: Int,
) {
    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        return S3AsyncClient.builder()
            .endpointOverride(URI.create(localEndpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("localstack", "localstack")
                )
            )
            .region(Region.of(region))
            .overrideConfiguration { builder ->
                builder.retryPolicy { policy ->
                    policy.numRetries(numRetries)
                        .backoffStrategy(FixedDelayBackoffStrategy.create(Duration.ofMillis(500)))
                }
            }
            .build()
    }
}