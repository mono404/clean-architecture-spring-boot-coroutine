package com.mono.backend.persistence.common

enum class Propagation {
    REQUIRED,
    REQUIRES_NEW,
    SUPPORTS,
    MANDATORY,
    NEVER,
    NOT_SUPPORTED,
    NESTED;
}