package com.mono.backend

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI

@Configuration
class S3Config(
    @Value("\${aws.s3.region}") private val region: String,
    @Value("\${aws.s3.localEndpoint}") private val localEndpoint: String,
    @Value("\${aws.s3.accessKey}") private val accessKey: String,
    @Value("\${aws.s3.secretKey}") private val secretKey: String,
) {
    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        return S3AsyncClient.builder()
            .endpointOverride(URI.create(localEndpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .region(Region.of(region))
            .build()
    }
}