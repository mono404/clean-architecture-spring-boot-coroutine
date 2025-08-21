package com.mono.backend.common.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object CoroutineUtils {
    /**
     * I/O 중심 작업(DB, 파일, 네트워크)
     * - 기본 64개의 스레드까지 확장 가능
     * - I/O Blocking 허용
     */
    val eventDispatchScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * CPU 연산 중심 (정렬, 파싱 등)
     * - 코어 수 기반(CPU * 2)
     * - Blocking 안됨 (성능 저하)
     */
    val batchProcessingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}