package org.example.dto.auth.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RefreshTokenRequest(
    @field:NotBlank
    @field:NotNull
    val refreshToken: String
)