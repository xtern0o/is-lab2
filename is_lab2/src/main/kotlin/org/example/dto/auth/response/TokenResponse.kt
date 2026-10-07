package org.example.dto.auth.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
