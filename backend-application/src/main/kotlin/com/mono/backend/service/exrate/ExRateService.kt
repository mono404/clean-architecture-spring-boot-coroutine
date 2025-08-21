package com.mono.backend.service.exrate

import com.mono.backend.port.infra.exrate.webclient.ExRatePersistencePort
import com.mono.backend.port.web.exrate.ExRateUseCase
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