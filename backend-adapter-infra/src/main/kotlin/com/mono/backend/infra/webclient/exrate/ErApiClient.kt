package com.mono.backend.infra.webclient.exrate

import com.fasterxml.jackson.databind.ObjectMapper
import com.mono.backend.infra.webclient.common.WebClientPair
import com.mono.backend.port.infra.exrate.webclient.ExRatePersistencePort
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import reactor.util.retry.Retry
import java.math.BigDecimal

@Service
@DependsOn("webClientFactory")
class ErApiClient(
    @Qualifier("erApiWebClientPair") webClientPair: WebClientPair,
) : ExRatePersistencePort {
    private val webClient = webClientPair.webClient
    private val retrySpec = Retry.backoff(
        webClientPair.properties.maxRetry, webClientPair.properties.retryDelay
    )

    override suspend fun getExRate(currency: String): BigDecimal = webClient
        .get()
        .uri(currency)
        .retrieve()
        .bodyToMono(String::class.java)
        .retryWhen(retrySpec)
        .map {
            val mapper = ObjectMapper()
            val data = mapper.readValue(it, ExRateData::class.java)
            data.rates["KRW"] ?: BigDecimal.ZERO
        }
        .awaitSingle()
}