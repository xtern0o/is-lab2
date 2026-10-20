package org.example.service.auth

import org.example.dto.auth.request.RefreshTokenRequest
import org.example.dto.auth.request.UserLoginRequest
import org.example.dto.auth.request.UserRegisterRequest
import org.example.dto.auth.response.TokenResponse
import org.example.dto.auth.response.UserResponse
import org.example.service.auth.JwtService
import org.example.service.auth.UserService
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userService: UserService
) {
    fun register(userRegisterRequest: UserRegisterRequest): TokenResponse {
        val user = userService.create(userRegisterRequest)
        return createTokens(user)
    }

    fun login(userLoginRequest: UserLoginRequest): TokenResponse {
        val user = userService.authenticate(userLoginRequest)
        return createTokens(user)
    }

    fun refresh(request: RefreshTokenRequest): TokenResponse {
        val userId = jwtService.getUserIdFromRefreshToken(request.refreshToken)
        val user = userService.getById(userId)

        return createTokens(user)
    }

    private fun createTokens(user: UserResponse): TokenResponse =
        TokenResponse(
            accessToken = jwtService.generateAccessToken(user),
            refreshToken = jwtService.generateRefreshToken(user),
        )

}