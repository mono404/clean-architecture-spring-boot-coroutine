package com.mono.backend.auth

import com.mono.backend.UpsertResponse
import com.mono.backend.exceptions.UnauthorizedException
import com.mono.backend.member.MemberResponse
import com.mono.backend.member.MemberService
import com.mono.backend.member.SocialProvider
import com.mono.backend.persistence.refreshtoken.RefreshTokenPersistencePort
import com.mono.backend.webclient.oauth.OAuthTokenVerifier
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberService: MemberService,
    private val jwtTokenUseCase: JwtTokenUseCase,
    private val oAuthVerifiers: List<OAuthTokenVerifier>,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort
) : AuthUseCase {
    override suspend fun loginOrJoin(request: SocialLoginRequest): UpsertResponse<LoginResponse> {
        val verifier = selectVerifier(request.provider)
        val userInfo = verifier.verify(request.accessToken)
        val member = memberService.findByOAuth(userInfo)

        return if (member != null) {
            val accessToken = jwtTokenUseCase.createAccessToken(member)
            val (refreshToken, refreshExp) = jwtTokenUseCase.createRefreshToken(member)
            refreshTokenPersistencePort.upsert(member.memberId, request.deviceId, refreshToken, refreshExp)

            UpsertResponse.updated(
                LoginResponse(
                    tokens = Tokens(accessToken, refreshToken),
                    member = MemberResponse.from(member)
                )
            )
        } else {
            val newMember = memberService.join(userInfo.provider, userInfo.providerId)
            val accessToken = jwtTokenUseCase.createAccessToken(newMember)
            val (refreshToken, refreshExp) = jwtTokenUseCase.createRefreshToken(newMember)
            refreshTokenPersistencePort.upsert(newMember.memberId, request.deviceId, refreshToken, refreshExp)

            UpsertResponse.created(
                LoginResponse(
                    tokens = Tokens(accessToken, refreshToken),
                    member = MemberResponse.from(newMember)
                )
            )
        }
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        if (!jwtTokenUseCase.validateToken(request.refreshToken)) {
            throw UnauthorizedException("Refresh token is invalid or expired")
        }

        val memberId = jwtTokenUseCase.getMemberId(request.refreshToken)
        val savedToken = refreshTokenPersistencePort.find(memberId, request.deviceId)
            ?: throw UnauthorizedException("Refresh token not found")

        if (savedToken != request.refreshToken) {
            throw UnauthorizedException("Refresh token mismatch")
        }

        val provider = jwtTokenUseCase.getProvider(request.refreshToken)
        val providerId = jwtTokenUseCase.getProviderId(request.refreshToken)

        val member = memberService.findByOAuth(provider, providerId) ?: throw UnauthorizedException("Member not found")
        val newAccessToken = jwtTokenUseCase.createAccessToken(member)
        val (newRefreshToken, refreshExp) = jwtTokenUseCase.createRefreshToken(member)
        refreshTokenPersistencePort.upsert(member.memberId, request.deviceId, newRefreshToken, refreshExp)

        return LoginResponse(
            tokens = Tokens(newAccessToken, newRefreshToken),
            member = MemberResponse.from(member)
        )
    }

    override suspend fun logout(request: RefreshTokenRequest) {
        if (!jwtTokenUseCase.validateToken(request.refreshToken)) return
        val memberId = jwtTokenUseCase.getMemberId(request.refreshToken)
        refreshTokenPersistencePort.delete(memberId, request.deviceId)
    }

    private fun selectVerifier(provider: SocialProvider): OAuthTokenVerifier {
        return oAuthVerifiers.first { it.support(provider) }
    }
}