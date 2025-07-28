package com.mono.backend.infra.webclient.oauth

import com.mono.backend.domain.auth.OAuthMemberInfo
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.infra.webclient.common.WebClientPair
import com.mono.backend.port.infra.oauth.webclient.OAuthTokenVerifier
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody
import reactor.util.retry.Retry

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
@DependsOn("webClientFactory")
class KakaoOAuthTokenVerifier(
    @Qualifier("kakaoOAuthWebClientPair") webClientPair: WebClientPair,
) : OAuthTokenVerifier {

    private val webClient = webClientPair.webClient
    private val retrySpec = Retry.backoff(
        webClientPair.properties.maxRetry, webClientPair.properties.retryDelay
    )

    override suspend fun verify(accessToken: String): OAuthMemberInfo {
        val res = webClient.get()
            .uri("/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .awaitBody<Map<String, Any>>()

        return OAuthMemberInfo(
            providerId = res["id"].toString(),
            provider = SocialProvider.KAKAO
        )
    }

    override fun support(provider: SocialProvider): Boolean {
        return provider == SocialProvider.KAKAO
    }

}