package org.example.dto.auth.response

import org.example.entity.enums.Role
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val role: Role,
)