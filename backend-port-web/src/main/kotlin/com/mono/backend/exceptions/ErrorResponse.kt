package com.mono.backend.exceptions

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)