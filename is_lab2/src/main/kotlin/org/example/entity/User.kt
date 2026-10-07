package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.example.dto.auth.response.UserResponse
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    @field:Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @field:Column(unique = true, nullable = false)
    @field:NotBlank
    var name: String,

    @field:Column(nullable = false)
    var passwordHash: String,
)

fun User.toResponse() = UserResponse(
    id = id!!,
    name = name
)