package com.mono.backend.infra.persistence.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager

@Configuration
@EnableR2dbcAuditing // Enable CreatedDate, LastModifiedDate annotation
class R2dbcConfig {
//    @Bean
//    @Primary
//    fun proxyConnectionFactory(baseConnectionFactory: ConnectionFactory): ConnectionFactory {
//        val log = logger()
//
//        val listener = object : ProxyExecutionListener {
//            override fun afterQuery(execInfo: QueryExecutionInfo) {
//                execInfo.queries.forEach {
//                    log.info("Executed SQL: ${it.query}")
//                }
//            }
//        }
//
//        return ProxyConnectionFactory.builder(baseConnectionFactory)
//            .listener(listener)
//            .build()
//    }

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}