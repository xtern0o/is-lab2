package org.example.dto.auth.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserLoginRequest(
    @field:NotNull
    val name: String,

    @field:NotNull
    @field:NotBlank
    @field:Size(min = 6, max = 64, message = "пароль от 6 до 64 символов!!")
    val password: String,
)
