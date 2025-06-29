package com.mono.backend.service.auth

import com.mono.backend.domain.member.Member
import com.mono.backend.domain.member.MemberRole
import com.mono.backend.domain.member.SocialProvider
import com.mono.backend.port.web.auth.JwtTokenUseCase
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${security.jwt.secret}")
    private val secret: String
) : JwtTokenUseCase {
    companion object {
        private const val DAY: Long = 60 * 60 * 1000 * 24
        private const val ACCESS_TOKEN_VALIDITY_SECONDS: Long = DAY * 7 // 1d
        private const val REFRESH_TOKEN_VALIDITY_SECONDS: Long = DAY * 30 // 7d
    }

    private val keyBytes = Decoders.BASE64.decode(secret)
    private val secretKey = Keys.hmacShaKeyFor(keyBytes)

    override suspend fun createAccessToken(member: Member) = createToken(member, ACCESS_TOKEN_VALIDITY_SECONDS)

    override suspend fun createRefreshToken(member: Member) =
        createToken(member, REFRESH_TOKEN_VALIDITY_SECONDS) to LocalDateTime.now().plusSeconds(
            REFRESH_TOKEN_VALIDITY_SECONDS
        )

    private suspend fun createToken(member: Member, validityMs: Long): String {
        val now = Date()
        val claims = Jwts.claims().setSubject(member.memberId.toString())
        claims["provider"] = member.provider
        claims["providerId"] = member.providerId
        claims["role"] = member.role.name
        val validity = Date(now.time + validityMs)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    override fun getMemberId(token: String) = getClaims(token).subject.toLong()
    override suspend fun getProvider(token: String) = SocialProvider.valueOf(getClaims(token)["provider"] as String)
    override suspend fun getProviderId(token: String) = getClaims(token)["providerId"] as String

    override fun validateToken(token: String): Boolean = try {
        !getClaims(token).expiration.before(Date())
    } catch (e: Exception) {
        false
    }

    override fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val memberId = claims.subject?.toLong()
        val role = claims["role"] as String?

        val authorities = when (role) {
            MemberRole.ADMIN.name -> listOf("ROLE_ADMIN", "ROLE_MEMBER")
            MemberRole.MEMBER.name -> listOf("ROLE_MEMBER")
            else -> listOf()
        }.map { SimpleGrantedAuthority(it) }
        return UsernamePasswordAuthenticationToken(memberId, null, authorities)
    }

    private fun getClaims(token: String) =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body
}