package com.mono.backend.exrate

import com.mono.backend.webclient.exrate.ExRatePersistencePort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ExRateService(
    private val exRatePersistencePort: ExRatePersistencePort
) : ExRateUseCase {
    override suspend fun getExRate(currency: String): BigDecimal {
        return exRatePersistencePort.getExRate(currency)
    }
}