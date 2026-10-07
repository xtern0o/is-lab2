package org.example.service

import org.example.dto.auth.request.UserLoginRequest
import org.example.dto.auth.request.UserRegisterRequest
import org.example.dto.auth.response.UserResponse
import org.example.entity.User
import org.example.entity.toResponse
import org.example.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun create(request: UserRegisterRequest): UserResponse {
        if (request.password != request.repeatedPassword) {
            throw IllegalArgumentException("пароли не совпадают")
        }
        if (userRepository.findByName(request.name) != null) {
            throw IllegalStateException("пользователь с таким именем уже существует")
        }

        val user = User(
            name = request.name,
            passwordHash = requireNotNull(passwordEncoder.encode(request.password)),
        )

        return userRepository.save(user).toResponse()
    }

    @Transactional(readOnly = true)
    fun authenticate(request: UserLoginRequest): UserResponse {
        val user = userRepository.findByName(request.name)

        if (user == null || !passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("неверное имя пользователя или пароль")
        }

        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): UserResponse =
        userRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("пользователь с id=${id} не найден")
            }
            .toResponse()
}
