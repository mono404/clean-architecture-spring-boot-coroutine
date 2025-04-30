package com.mono.backend.webclient.common.adapter

import org.springframework.web.reactive.function.client.WebClient

interface WebClientDefaultAdapter {
    fun buildAdditional(): WebClient.Builder.() -> Unit = {}
}