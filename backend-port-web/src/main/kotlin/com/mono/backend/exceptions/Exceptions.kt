package com.mono.backend.exceptions

class UnauthorizedException(
    override val message: String = "Unauthorized"
) : RuntimeException(message)

class ForbiddenException(
    override val message: String = "Forbidden"
) : RuntimeException(message)

class BadRequestException(
    override val message: String = "Bad Request"
) : RuntimeException(message)