package com.mono.backend.webclient.exrate

import java.math.BigDecimal

interface ExRatePersistencePort {
    suspend fun getExRate(currency: String): BigDecimal
}