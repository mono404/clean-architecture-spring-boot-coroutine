package com.mono.backend.infra.webclient.oauth

import com.mono.backend.domain.auth.OAuthMemberInfo
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.infra.webclient.common.WebClientPair
import com.mono.backend.port.infra.oauth.webclient.OAuthTokenVerifier
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody
import reactor.util.retry.Retry

@Service
@DependsOn("webClientFactory")
class GoogleOAuthTokenVerifier(
    @Qualifier("googleOAuthWebClientPair") webClientPair: WebClientPair,
) : OAuthTokenVerifier {

    private val webClient = webClientPair.webClient
    private val retrySpec = Retry.backoff(
        webClientPair.properties.maxRetry, webClientPair.properties.retryDelay
    )

    override suspend fun verify(accessToken: String): OAuthMemberInfo {
        val res = webClient.get()
            .uri("/tokeninfo?id_token=$accessToken")
            .retrieve()
            .awaitBody<Map<String, String>>()

        return OAuthMemberInfo(
            providerId = res["sub"]!!,
            provider = SocialProvider.GOOGLE
        )
    }

    override fun support(provider: SocialProvider): Boolean {
        return provider == SocialProvider.GOOGLE
    }

}