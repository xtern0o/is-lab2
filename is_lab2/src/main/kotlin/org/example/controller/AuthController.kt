package org.example.controller

import jakarta.validation.Valid
import org.example.dto.auth.request.RefreshTokenRequest
import org.example.dto.auth.request.UserLoginRequest
import org.example.dto.auth.request.UserRegisterRequest
import org.example.dto.auth.response.TokenResponse
import org.example.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: UserRegisterRequest,
    ): TokenResponse =
        authService.register(request)

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLoginRequest,
    ): TokenResponse =
        authService.login(request)

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): TokenResponse =
        authService.refresh(request)
}