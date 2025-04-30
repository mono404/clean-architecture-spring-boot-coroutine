package com.mono.backend.exrate

import java.math.BigDecimal

interface ExRateUseCase {
    suspend fun getExRate(currency: String) : BigDecimal
}