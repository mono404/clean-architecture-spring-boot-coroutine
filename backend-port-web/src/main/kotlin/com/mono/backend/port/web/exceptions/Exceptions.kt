package com.mono.backend.port.web.exceptions

class UnauthorizedException(
    override val message: String = "Unauthorized"
) : RuntimeException(message)

class ForbiddenException(
    override val message: String = "Forbidden"
) : RuntimeException(message)

class BadRequestException(
    override val message: String = "Bad Request"
) : RuntimeException(message)

class NotFoundException(
    override val message: String = "Not Found"
) : RuntimeException(message)