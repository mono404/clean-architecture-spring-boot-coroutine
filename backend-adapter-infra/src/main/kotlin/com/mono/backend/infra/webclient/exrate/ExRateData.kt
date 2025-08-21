package com.mono.backend.infra.webclient.exrate

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExRateData(
    val result: String = "",
    val rates: Map<String, BigDecimal> = emptyMap()
)