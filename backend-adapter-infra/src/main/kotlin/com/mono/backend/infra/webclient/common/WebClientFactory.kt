package com.mono.backend.infra.webclient.common

import com.mono.backend.common.log.logger
import com.mono.backend.common.util.CommonUtils.applyWhen
import com.mono.backend.common.util.CommonUtils.convertToBeanName
import com.mono.backend.infra.webclient.common.adapter.WebClientDefaultAdapter
import com.mono.backend.infra.webclient.common.properties.WebClientProperties
import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.LoopResources
import java.util.concurrent.TimeUnit

@Component
@ConfigurationProperties(prefix = "web-client")
class WebClientFactory(
    val defaultProperties: WebClientProperties = WebClientProperties(),
    val services: Map<String, WebClientProperties>,
    private val adapterProvider: ObjectProvider<WebClientDefaultAdapter>,
) : BeanFactoryAware {
    private val log = logger()

    private lateinit var beanFactory: ConfigurableBeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        Assert.state(beanFactory is ConfigurableBeanFactory, "wrong bean factory type")
        this.beanFactory = beanFactory as ConfigurableBeanFactory
    }

    @PostConstruct
    fun configure() {
        services.entries.forEach { (name, properties) ->
            properties.overrideProperties(name, defaultProperties)
            registerPropertyBean(name, properties)
            registerWebClientBean(name, properties)
        }
    }

    private fun registerPropertyBean(
        name: String,
        properties: WebClientProperties,
    ) {
        val propertyBeanName = "${name.convertToBeanName()}WebClientProperties"
        beanFactory.registerSingleton(propertyBeanName, properties)
        log.info(
            "create-webClient-property, name={}, beanName={}, properties={}",
            name,
            propertyBeanName,
            properties,
        )
    }

    private fun registerWebClientBean(
        name: String,
        properties: WebClientProperties,
    ) {
        val webClientBeanName = "${name.convertToBeanName()}${WebClientPair::class.simpleName}"
        val webClientPair = createWebClient(properties)
        beanFactory.registerSingleton(webClientBeanName, webClientPair)
        log.info("create-webClient, name={}, beanName={}", name, webClientBeanName)
    }

    fun createWebClient(
        properties: WebClientProperties,
        block: WebClient.Builder.() -> Unit = {},
    ): WebClientPair =
        WebClientPair(
            WebClient
                .builder()
                .clientConnector(properties.toConnector())
                .baseUrl(properties.url)
                .apply(
                    adapterProvider.ifAvailable
                        ?.apply { log.info("apply-default-adapter") }
                        ?.buildAdditional()
                        ?: {},
                ).apply(block)
                .build(),
            properties,
        )

    private fun WebClientProperties.toConnector(): ReactorClientHttpConnector {
        log.info("initial-web-client-properties, {}", this)

        val sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()

        val provider =
            ConnectionProvider
                .builder("$name-provider")
                .apply {
                    val maxConnection = maxConnection()
                    if (maxConnection != null) {
                        maxConnections(maxConnection)
                    }
                }.applyWhen(maxIdleTime != null) {
                    maxIdleTime(maxIdleTime!!)
                }.applyWhen(maxLifeTime != null) {
                    maxLifeTime(maxLifeTime!!)
                }.build()

        log.info("initial-web-client-properties, name={}, maxConnections={}", name, provider.maxConnections())

        val httpClient =
            HttpClient
                .create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .secure { it.sslContext(sslContext) }
                .doOnConnected {
                    it
                        .addHandlerLast(ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                }.runOn(LoopResources.create("reactor-webclient"))
        return ReactorClientHttpConnector(httpClient)
    }
}
