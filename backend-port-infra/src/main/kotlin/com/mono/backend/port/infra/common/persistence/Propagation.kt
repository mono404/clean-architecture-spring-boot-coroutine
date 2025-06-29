package com.mono.backend.port.infra.common.persistence

enum class Propagation {
    REQUIRED,
    REQUIRES_NEW,
    SUPPORTS,
    MANDATORY,
    NEVER,
    NOT_SUPPORTED,
    NESTED;
}