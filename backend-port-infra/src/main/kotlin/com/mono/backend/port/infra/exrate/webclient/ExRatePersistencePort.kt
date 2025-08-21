package com.mono.backend.port.infra.exrate.webclient

import java.math.BigDecimal

interface ExRatePersistencePort {
    suspend fun getExRate(currency: String): BigDecimal
}