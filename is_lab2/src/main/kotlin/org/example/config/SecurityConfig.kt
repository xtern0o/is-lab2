package org.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize("/api/auth/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }

        return http.build()
    }

    @Bean
    fun jwtSecretKey(
        @Value("\${jwt.secret}") secret: String,
    ): SecretKey {
        require(secret.toByteArray().size >= 32) {
            "JWT_SECRET должен содержать минимум 32 байта по спецификации!!"
        }

        return SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    }

    @Bean
    fun jwtEncoder(key: SecretKey): JwtEncoder =
        NimbusJwtEncoder
            .withSecretKey(key)
            .algorithm(MacAlgorithm.HS256)
            .build()

    @Bean("accessJwtDecoder")
    @Primary
    fun accessJwtDecoder(key: SecretKey): JwtDecoder {
        val decoder = NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

        val accessTokenValidator = OAuth2TokenValidator<Jwt> { jwt ->
            if (jwt.getClaimAsString("type") == "access") {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error(
                        "invalid_token",
                        "ожидался access token!!",
                        null,
                    ),
                )
            }
        }

        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer("is_lab1"),
                accessTokenValidator,
            ),
        )

        return decoder
    }

    @Bean("refreshJwtDecoder")
    fun refreshJwtDecoder(key: SecretKey): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
            .apply {
                setJwtValidator(
                    JwtValidators.createDefaultWithIssuer("is_lab1"),
                )
            }
}
