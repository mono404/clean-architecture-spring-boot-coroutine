package com.mono.backend.port.web.exrate

import java.math.BigDecimal

interface ExRateUseCase {
    suspend fun getExRate(currency: String) : BigDecimal
}