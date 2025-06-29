package com.mono.backend.infra.persistence.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@Configuration
@EnableR2dbcAuditing // Enable CreatedDate, LastModifiedDate annotation
class R2dbcConfig