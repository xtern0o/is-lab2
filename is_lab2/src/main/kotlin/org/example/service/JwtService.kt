package org.example.service

import org.example.dto.auth.response.UserResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,

    @param:Qualifier("refreshJwtDecoder")
    private val refreshDecoder: JwtDecoder,

    @param:Value("\${jwt.access-ttl-seconds}")
    private val accessTtl: Long,

    @param:Value("\${jwt.refresh-ttl-seconds}")
    private val refreshTtl: Long,
) {

    fun generateAccessToken(user: UserResponse): String =
        generateToken(user, accessTtl, "access")

    fun generateRefreshToken(user: UserResponse): String =
        generateToken(user, refreshTtl, "refresh")

    fun getUserIdFromRefreshToken(token: String): UUID {
        val jwt = try {
            refreshDecoder.decode(token)
        } catch (_: JwtException) {
            throw IllegalArgumentException("невалидный refresh-токен")
        }

        if (jwt.getClaimAsString("type") != "refresh") {
            throw IllegalArgumentException("ожидался refresh-токен")
        }

        return try {
            UUID.fromString(jwt.subject)
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("неверный пользователь в refresh-токене")
        }
    }

    private fun generateToken(
        user: UserResponse,
        ttl: Long,
        type: String,
    ): String {
        val now = Instant.now()

        val claims = JwtClaimsSet.builder()
            .issuer("is_lab1")
            .subject(user.id.toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(ttl))
            .claim("name", user.name)
            .claim("type", type)
            .build()

        val header = JwsHeader
            .with(MacAlgorithm.HS256)
            .type("JWT")
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue

    }
}