package com.mono.backend.infra.webclient.common

import com.mono.backend.infra.webclient.common.properties.WebClientProperties
import org.springframework.web.reactive.function.client.WebClient

class WebClientPair(
    val webClient: WebClient,
    val properties: WebClientProperties,
)