package com.mono.backend

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

@Configuration
class S3Config(
    @Value("\${aws.region}") private val region: String,
) {
    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        val builder = S3AsyncClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(region))
        return builder.build()
    }
}